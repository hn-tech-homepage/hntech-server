package hntech.hntechserver.domain.category

import hntech.hntechserver.common.ARCHIVE
import hntech.hntechserver.common.BoolResponse
import hntech.hntechserver.common.MAX_MAIN_CATEGORY_COUNT
import hntech.hntechserver.common.PRODUCT
import hntech.hntechserver.config.FILE_TYPE_CATEGORY
import hntech.hntechserver.domain.file.FileService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val fileService: FileService
) {
    // 카테고리명 중복 체크
    private fun checkCategoryName(name: String) {
        if (categoryRepository.existsByCategoryName(name))
            throw CategoryException(DUPLICATE_CATEGORY_NAME)
    }

    // 메인에 등록된 카테고리 개수 체크
    private fun checkMainCategoryCount() {
        if (categoryRepository.countMainCategories() > MAX_MAIN_CATEGORY_COUNT)
            throw CategoryException(MAXIMUM_NUMBER_OF_CATEGORIES)
    }
    
    // 마지막 순서의 카테고리 조회
    private fun getLastCategory(): Category? =
        categoryRepository.findFirstByOrderBySequenceDesc()

    private fun toListResponse() = ProductCategoryListResponse(
            getAllCategories()
                .sortedBy { it.sequence }
                .map { ProductCategoryResponse(it) }
        )


    /**
     * 카테고리 생성
     */
    @Transactional
    fun createCategory(form: CreateCategoryForm): ProductCategoryResponse {

        checkCategoryName(form.categoryName)
        if (form.showInMain == "true") checkMainCategoryCount()

        categoryRepository.adjustSequenceToRightAll()

        val savedCategory = categoryRepository.save(Category(
            categoryName = form.categoryName,
            type = form.type,
            showInMain = form.showInMain,
            file = form.image?.let { fileService.saveFile(it, FILE_TYPE_CATEGORY) },
            role = form.role
        ))

        if (form.parentName == form.categoryName) throw CategoryException(DUPLICATE_PARENT_CHILD)

        // 만약 중분류 카테고리로써, 부모 카테고리를 지정해야 한다면 수행
        if (form.role == "child") {
            val parentCategory = getCategory(form.parentName)
            parentCategory.addChild(savedCategory)
            savedCategory.mappingParent(parentCategory)
        }

        return ProductCategoryResponse(savedCategory)
    }

    /**
     * 카테고리 조회
     * 모든 조회는 sequence 기준 정렬
     */
    // 카테고리 전체 조회 (제품, 자료실 카테고리 모두 / id, 이름 응답)
    private fun getAllCategories(): List<Category> =
        getAllByType(ARCHIVE).union(getAllByType(PRODUCT)).toList()

    // 카테고리 전체 조회 (제품, 자료실 카테고리 모두 / id, 이름 응답)
    fun getAllCategoriesToDto() = AllCategoryListResponse(
        getAllCategories().map { ArchiveCategoryResponse(it) }
    )

    // 타입으로 카테고리 전체 조회
    private fun getAllByType(type: String): List<Category> =
        categoryRepository.findAllByType(type)

    // 제품 카테고리 전체 조회
    fun getAllProductCategories() =
        ProductCategoryListResponse(
            getAllByType(PRODUCT)
                .map { ProductCategoryResponse(it) }
        )

    // 자료실 카테고리 전체 조회
    fun getAllArchiveCategories() =
        ArchiveCategoryListResponse(
            getAllByType(ARCHIVE).map { ArchiveCategoryResponse(it) }
        )

    // 메인에 표시될 카테고리만 조회
    fun getMainCategories() =
        ProductCategoryListResponse(
            categoryRepository.findAllByShowInMain()
                .map { ProductCategoryResponse(it) }
        )
    
    // 카테고리 ID로 조회
    fun getCategory(id: Long): Category =
        categoryRepository.findById(id).orElseThrow {
            throw CategoryException(CATEGORY_NOT_FOUND)
        }
    
    // 카테고리 이름으로 조회
    fun getCategory(categoryName: String): Category =
        categoryRepository.findByCategoryName(categoryName) ?:
            throw CategoryException(CATEGORY_NOT_FOUND)
    
    // 대분류 카테고리 조회
    fun getParentProductCategories() = ProductCategoryListResponse(
        categoryRepository.findAllParents()
            .map { ProductCategoryResponse(it) }
    )

    // 대분류로 중분류 카테고리 조회
    fun getChildrenProductCategories(parent: String) = ProductCategoryListResponse(
        categoryRepository.findAllChildren(parent)
            .map { ProductCategoryResponse(it) }
    )

    /**
     * 카테고리 수정
     */
    @Transactional
    fun updateCategory(
        categoryId: Long,
        form: UpdateCategoryForm
    ): ProductCategoryListResponse {
        if (form.showInMain == "true") checkMainCategoryCount()

        val category: Category = getCategory(categoryId)

        // 수정하려는 이름이 현재 이름과 같지 않으면 이름 중복 체크
        if (category.categoryName != form.categoryName)
            checkCategoryName(form.categoryName)

        category.update(
            categoryName = form.categoryName,
            showInMain = form.showInMain,
            file = form.image?.let { fileService.saveFile(it, FILE_TYPE_CATEGORY) }
        )

        return toListResponse()
    }
    
    /**
     * 카테고리 순서 변경
     * 바꿀 카테고리를 목표 카테고리의 뒤에 위치시킨다.
     */
    @Transactional
    fun updateCategorySequence(form: UpdateCategorySequenceForm): ProductCategoryListResponse {
        val currentSequence = getCategory(form.currentCategoryId).sequence
        val targetSequence: Int = when(form.targetCategoryId) {
            0L -> 0 // 타겟 id가 0이면 맨 앞으로
            else -> getCategory(form.targetCategoryId).sequence
        }
        // 순서 변경 전 sequence 조정(왼쪽->오른쪽 / 오른쪽->왼쪽)
        if (currentSequence < targetSequence) {
            categoryRepository.adjustSequenceToLeft(currentSequence, targetSequence)
            getCategory(form.currentCategoryId).update(sequence = targetSequence)
        } else {
            categoryRepository.adjustSequenceToRight(targetSequence, currentSequence)
            getCategory(form.currentCategoryId).update(sequence = targetSequence + 1)
        }
        categoryRepository.flush()

        return toListResponse()
    }

    /**
     * 카테고리 삭제
     */
    @Transactional
    fun deleteCategory(categoryId: Long): BoolResponse {
        // 카테고리 순서 조정
        categoryRepository.adjustSequenceToLeftAll(getCategory(categoryId).sequence)
        categoryRepository.deleteById(categoryId)
        return BoolResponse(true)
    }
}