package hntech.hntechserver.category

import hntech.hntechserver.CategoryException
import hntech.hntechserver.file.FileService
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val fileService: FileService,
    ) {
    private val ARCHIVE_TYPE = "자료실"
    private val ITEM_TYPE = "제품"

    /**
     * 카테고리 생성
     */
    // 카테고리 중복 여부 검사
    private fun checkCategoryName(name: String) {
        if (categoryRepository.existsByCategoryName(name))
            throw CategoryException("카테고리 이름 중복")
    }

    // 제품 카테고리 생성
    fun createItemCategory(form: CreateItemCategoryRequest): ItemCategoryResponse {
        // 제품 대표 이미지 업로드
        val savedImage = fileService.saveFile(form.image)

        // 카테고리 중복 여부 검사
        checkCategoryName(form.categoryName)

        val category = Category(
            categoryName = form.categoryName,
            categoryImagePath = savedImage.savedPath,
            type = ITEM_TYPE
        )

        categoryRepository.save(category)
        return convertItemDto(category)
    }


    // 자료실 카테고리 생성
    fun createArchiveCategory(form: CreateArchiveCategoryRequest): ArchiveCategoryResponse {
        // 카테고리 중복 여부 검사
        checkCategoryName(form.categoryName)
        val category = Category(categoryName = form.categoryName, type = ARCHIVE_TYPE)
        categoryRepository.save(category)
        return convertArchiveDto(category)
    }

    /**
     * 카테고리 조회
     */
    // 자료실 카테고리 전체 조회
    fun getAllArchiveCategories(): ArchiveCategoryListResponse {
        val result = mutableListOf<ArchiveCategoryResponse>()
        categoryRepository.findAllByType(ARCHIVE_TYPE).forEach {
            result.add(ArchiveCategoryResponse(it.categoryName))
        }
        return ArchiveCategoryListResponse(result)
    }

    // 제품 카테고리 전체 조회
    fun getAllItemCategories(): ItemCategoryListResponse {
        val result = mutableListOf<ItemCategoryResponse>()
        categoryRepository.findAllByType(ITEM_TYPE).forEach {
            result.add(ItemCategoryResponse(it.categoryName, it.categoryImagePath))
        }
        return ItemCategoryListResponse(result)
    }

    /**
     * 카테고리 수정
     */

    /**
     * 카테고리 삭제
     */

}