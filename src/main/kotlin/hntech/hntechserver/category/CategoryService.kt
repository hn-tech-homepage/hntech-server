package hntech.hntechserver.category

import hntech.hntechserver.CategoryException
import hntech.hntechserver.file.File
import hntech.hntechserver.file.FileService
import hntech.hntechserver.utils.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val fileService: FileService,
    )
{
    private val ARCHIVE_TYPE = "자료실"
    private val ITEM_TYPE = "제품"
    val log = logger()

    /**
     * 카테고리 생성
     */
    // 카테고리 중복 여부 검사
    private fun checkCategoryName(name: String) {
        if (categoryRepository.existsByCategoryName(name))
            throw CategoryException("카테고리 이름 중복")
    }

    // 제품 카테고리 생성
    fun createItemCategory(form: ItemCategoryRequest): Category {
        if (form.image.isEmpty) throw CategoryException("대표 이미지를 설정해야 합니다.")

        // 제품 대표 이미지 업로드
        val savedImage: File = fileService.saveFile(form.image)

        // 카테고리 중복 여부 검사
        checkCategoryName(form.categoryName)

        val category = Category(
            categoryName = form.categoryName,
            categoryImagePath = savedImage.savedPath,
            type = ITEM_TYPE
        )

        categoryRepository.save(category)
        return category
    }


    // 자료실 카테고리 생성
    fun createArchiveCategory(form: ArchiveCategoryRequest): Category {
        // 카테고리 중복 여부 검사
        checkCategoryName(form.categoryName)
        val category = Category(categoryName = form.categoryName, type = ARCHIVE_TYPE)
        categoryRepository.save(category)
        return category
    }

    /**
     * 카테고리 조회
     */
    // 제품 카테고리 전체 조회
    @Transactional(readOnly = true)
    fun getAllItemCategories(): List<Category> = categoryRepository.findAllByType(ITEM_TYPE)

    // 자료실 카테고리 전체 조회
    @Transactional(readOnly = true)
    fun getAllArchiveCategories(): List<Category> = categoryRepository.findAllByType(ARCHIVE_TYPE)



    /**
     * 카테고리 수정
     */
    // 제품 카테고리 수정
    fun updateItemCategory(categoryId: Long, form: ItemCategoryRequest): List<Category> {
        val category: Category = categoryRepository.findById(categoryId).get()

        // 이름과 대표 사진 둘 다 변경
        if (!form.image.isEmpty) {
            // 1. 기존 사진 삭제
            fileService.deleteFile(category.categoryImagePath)

            // 2. 새로운 사진 저장
            val newImage = fileService.saveFile(form.image)
            category.update(form.categoryName, newImage.savedPath)

        } else {
            category.update(form.categoryName)
        }
        return getAllItemCategories()
    }

    // 자료실 카테고리 수정
    fun updateArchiveCategory(categoryId: Long, form: ArchiveCategoryRequest): List<Category> {
        val category: Category = categoryRepository.findById(categoryId).get()
        category.update(form.categoryName)
        return getAllArchiveCategories()
    }

    /**
     * 카테고리 삭제
     */
    // 제품 카테고리 삭제
    fun deleteCategory(categoryId: Long): Boolean {
        categoryRepository.deleteById(categoryId)
        return !categoryRepository.existsById(categoryId)
    }



}