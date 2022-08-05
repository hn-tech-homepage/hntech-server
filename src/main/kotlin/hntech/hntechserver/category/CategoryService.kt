package hntech.hntechserver.category

import hntech.hntechserver.file.FileService
import hntech.hntechserver.utils.config.MAX_MAIN_CATEGORY_COUNT
import hntech.hntechserver.utils.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val fileService: FileService
) {
    val log = logger()

    // 카테고리명 중복 체크
    private fun checkCategoryName(name: String) {
        if (categoryRepository.existsByCategoryName(name)) throw CategoryException(DUPLICATE_CATEGORY_NAME)
    }

    // 메인에 등록된 카테고리 개수 체크
    private fun checkMainCategoryCount() {
        if (categoryRepository.countMainCategories() >= MAX_MAIN_CATEGORY_COUNT)
            throw CategoryException(MAXIMUM_NUMBER_OF_CATEGORIES)
    }
    
    // 마지막 카테고리 조회
    private fun getLastCategory(): Category? = categoryRepository.findFirstByOrderBySequenceDesc()

    // 카테고리 생성
    fun createCategory(form: CategoryCreateForm): Category {
        checkCategoryName(form.categoryName)

        val category = categoryRepository.save(
            Category(
                categoryName = form.categoryName,
                sequence = getLastCategory()?.let { it.sequence + 1 } ?: run { 1 }
            )
        )
        category.update(fileService.saveFile(form.image))
        return category
    }

    /**
     * 카테고리 조회
     */
    // 카테고리 전체 조회
    @Transactional(readOnly = true)
    fun getAllCategories(): List<Category> = categoryRepository.findAllByOrderBySequence()

    // 메인에 표시될 카테고리만 조회
    @Transactional(readOnly = true)
    fun getMainCategories(): List<Category> = categoryRepository.findAllByShowInMain()
    
    // 카테고리 ID로 조회
    @Transactional(readOnly = true)
    fun getCategory(id: Long): Category =
        categoryRepository.findById(id).orElseThrow { throw CategoryException(CATEGORY_NOT_FOUND) }
    
    // 카테고리 이름으로 조회
    @Transactional(readOnly = true)
    fun getCategory(categoryName: String): Category =
        categoryRepository.findByCategoryName(categoryName) ?: throw CategoryException(CATEGORY_NOT_FOUND)

    /**
     * 카테고리 수정
     */
    // 카테고리 수정
    fun updateCategory(categoryId: Long, form: CategoryUpdateForm): List<Category> {
        log.info("메인 카테고리 개수 : {}", categoryRepository.countMainCategories())
        checkMainCategoryCount()

        val category: Category = getCategory(categoryId)

        // 수정하려는 이름이 현재 이름과 같지 않으면 이름 중복 체크
        if (category.categoryName != form.categoryName) checkCategoryName(form.categoryName)

        val updatedFile = fileService.updateFile(
            oldFile = category.file!!,
            newFile = form.image
        )
        category.update(form.categoryName, form.showInMain, updatedFile)
        return getAllCategories()
    }
    
    /**
     * 카테고리 순서 변경
     * 바꿀 카테고리를 목표 카테고리의 앞에 위치시킨다.
     */
    fun updateCategorySequence(categoryId: Long, targetCategoryId: Long): List<Category> {
        val currentSequence: Int = getCategory(categoryId).sequence
        val targetSequence: Int = when(targetCategoryId) {
            // 타겟 id가 0이면 맨 뒤로 보냄
            0L -> getLastCategory()!!.sequence
            else -> getCategory(targetCategoryId).sequence
        }
        /**
         * 순서 변경 전 sequence 조정
         * 좌측으로 바꿀 경우 target의 우측 카테고리들의 sequence + 1
         * 우측으로 바꿀 경우 target의 좌측 카테고리들의 sequence - 1
         */
        if (currentSequence > targetSequence) categoryRepository.adjustSequenceToRight(targetSequence)
        else categoryRepository.adjustSequenceToLeft(targetSequence)

        // 바꿀 카테고리의 sequence를 기존 targetCategory의 sequence로 순서 변경
        getCategory(categoryId).update(targetSequence)

        return getAllCategories()
    }

    // 카테고리 삭제
    fun deleteCategory(categoryId: Long) {
        val findCategory = getCategory(categoryId)
        
        // 카테고리에 물려있는 파일들 삭제
        fileService.deleteFile(findCategory.file!!)
        findCategory.archives.forEach { fileService.deleteAllFiles(it.files) }
        findCategory.products.forEach { fileService.deleteAllFiles(it.files) }

        // 카테고리 순서 조정
        categoryRepository.adjustSequenceToLeft(findCategory.sequence)

        categoryRepository.deleteById(categoryId)
    }
}