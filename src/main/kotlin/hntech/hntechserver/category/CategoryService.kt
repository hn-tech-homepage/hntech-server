package hntech.hntechserver.category

import hntech.hntechserver.file.FileService
import hntech.hntechserver.utils.error.CATEGORY_NOT_FOUND
import hntech.hntechserver.utils.error.CategoryException
import hntech.hntechserver.utils.error.DUPLICATE_CATEGORY_NAME
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

    // 카테고리 중복 여부 검사
    private fun checkCategoryName(name: String) {
        if (categoryRepository.existsByCategoryName(name)) throw CategoryException(DUPLICATE_CATEGORY_NAME)
    }

    // 카테고리 생성
    fun createCategory(form: CategoryRequest): Category {
        // 카테고리 중복 여부 검사
        checkCategoryName(form.categoryName)

        // 카테고리 대표 이미지 업로드
        val savedImage = fileService.saveFile(form.image)

        return categoryRepository.save(
            Category(
                categoryName = form.categoryName,
                categoryFile = savedImage,
        ))
    }

    /**
     * 카테고리 조회
     */
    // 카테고리 전체 조회
    @Transactional(readOnly = true)
    fun getAllCategories(): List<Category> = categoryRepository.findAll()
    
    // 카테고리 ID로 조회
    @Transactional(readOnly = true)
    fun getCategory(id: Long): Category =
        categoryRepository.findById(id).orElseThrow { throw CategoryException(CATEGORY_NOT_FOUND) }
    
    // 카테고리 이름으로 조회
    @Transactional(readOnly = true)
    fun getCategory(categoryName: String): Category =
        categoryRepository.findByCategoryName(categoryName) ?: throw CategoryException(CATEGORY_NOT_FOUND)

    // 카테고리 수정
    fun updateCategory(categoryId: Long, form: CategoryRequest): List<Category> {

        checkCategoryName(form.categoryName)

        val category: Category = getCategory(categoryId)

        // 이름과 대표 사진 둘 다 변경
        if (!form.image.isEmpty) {
            fileService.updateFile(fileEntity = category.categoryFile!!, newFile = form.image)
            category.update(form.categoryName, category.categoryFile!!)
        } else {
            category.update(form.categoryName)
        }
        return getAllCategories()
    }

    // 카테고리 삭제
    fun deleteCategory(categoryId: Long) {
        val findCategory = getCategory(categoryId)
        
        // 카테고리에 물려있는 파일들 삭제
        findCategory.archives.forEach { fileService.deleteAllFiles(it.files) }
        findCategory.products.forEach { fileService.deleteAllFiles(it.files) }
        categoryRepository.deleteById(categoryId)
    }
}