package hntech.hntechserver.category

import hntech.hntechserver.file.FileService
import hntech.hntechserver.utils.error.CategoryException
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
    private final val UPLOAD_PATH = "C:\\dev\\"

    // 카테고리 중복 여부 검사
    private fun checkCategoryName(name: String) {
        if (categoryRepository.existsByCategoryName(name)) throw CategoryException("카테고리 이름 중복")
    }

    // 카테고리 생성
    @Transactional
    fun createCategory(form: CategoryRequest): Category {

        // 카테고리 중복 여부 검사
        checkCategoryName(form.categoryName)

        // 카테고리 대표 이미지 업로드
        val savedImage = fileService.uploadAndMakeEntity(form.image)

        return categoryRepository.save(
            Category(
                categoryName = form.categoryName,
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
    fun getCategory(id: Long): Category
    = categoryRepository.findById(id).orElseThrow { throw CategoryException("해당 카테고리가 존재하지 않습니다.") }
    
    // 카테고리 이름으로 조회
    @Transactional(readOnly = true)
    fun getCategory(categoryName: String): Category
    = categoryRepository.findByCategoryName(categoryName) ?: throw CategoryException("해당 카테고리가 존재하지 않습니다.")

    // 카테고리 수정
    @Transactional
    fun updateCategory(categoryId: Long, form: CategoryRequest): List<Category> {

        checkCategoryName(form.categoryName)

        val category: Category = categoryRepository.findById(categoryId)
            .orElseThrow { throw NoSuchElementException("해당 카테고리가 존재하지 않습니다.") }

        // 이름과 대표 사진 둘 다 변경
        if (!form.image.isEmpty) {
            // 1. 기존 파일 삭제
            fileService.deleteFile(category.categoryFile!!)

            // 2. 새로운 파일 저장 후 디비 update
            val newImage = fileService.uploadAndMakeEntity(form.image)
            category.update(form.categoryName, category.categoryFile!!)
        } else {
            category.update(form.categoryName)
        }
        return getAllCategories()
    }

    // 카테고리 삭제
    @Transactional
    fun deleteCategory(categoryId: Long) {
        val category = getCategory(categoryId)
        fileService.deleteFile(category.categoryFile!!)
        fileService.deleteAllFiles(category)
        categoryRepository.deleteById(categoryId)
    }
}