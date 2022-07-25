package hntech.hntechserver.category

import hntech.hntechserver.utils.BoolResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/category")
class CategoryController(private val categoryService: CategoryService) {

    /**
     * 카테고리 생성
     */
    // 제품 카테고리 생성 : form-data 방식으로 file value에는 이미지를, text value에는 이름을 넣는다.
    @PostMapping("/item")
    fun createItemCategory(
        @ModelAttribute form: ItemCategoryRequest
    ): ItemCategoryResponse
    = convertItemDto(categoryService.createItemCategory(form))


    // 자료실 카테고리 생성
    @PostMapping("/archive")
    fun createArchiveCategory(
        @RequestBody form: ArchiveCategoryRequest
    ): ArchiveCategoryResponse
    = convertArchiveDto(categoryService.createArchiveCategory(form))


    /**
     * 카테고리 조회
     */
    // 제품 카테고리 전체 조회
    @GetMapping("/item")
    fun getAllItemCategories()
    = ItemCategoryListResponse(
        categoryService.getAllItemCategories().map { convertItemDto(it) }
    )



    // 자료실 카테고리 전체 조회
    @GetMapping("/archive")
    fun getAllArchiveCategories()
    = ArchiveCategoryListResponse(
        categoryService.getAllArchiveCategories().map { convertArchiveDto(it) }
    )

    /**
     * 카테고리 수정 -> 수정된 제품을 포함한 카테고리 리스트 반환
     */
    // 제품 카테고리 수정
    @PutMapping("/item/{categoryId}")
    fun updateItemCategory(
        @PathVariable("categoryId") categoryId: Long,
        @RequestBody form: ItemCategoryRequest)
    = ItemCategoryListResponse(
        categoryService.updateItemCategory(categoryId, form).map { convertItemDto(it) }
    )

    // 자료실 카테고리 수정
    @PutMapping("/archive/{categoryId}")
    fun updateArchiveCategory(
        @PathVariable("categoryId") categoryId: Long,
        @RequestBody form: ArchiveCategoryRequest)
    = ArchiveCategoryListResponse(
        categoryService.updateArchiveCategory(categoryId, form).map { convertArchiveDto(it) }
    )


    /**
     * 카테고리 삭제
     */
    @DeleteMapping("/{categoryId}")
    fun deleteCategory(@PathVariable("categoryId") categoryId: Long)
    = BoolResponse(
        categoryService.deleteCategory(categoryId)
    )

}