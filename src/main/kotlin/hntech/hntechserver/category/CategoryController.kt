package hntech.hntechserver.category

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/category")
class CategoryController(private val categoryService: CategoryService) {

    // 카테고리 생성 : form-data 방식으로 file value에는 이미지를, text value에는 이름을 넣는다.
    @PostMapping
    fun createCategory(@ModelAttribute form: CategoryRequest): CategoryResponse
    = convertDto(categoryService.createCategory(form))

    // 카테고리 전체 조회
    @GetMapping
    fun getAllCategories(): List<CategoryResponse>
    = categoryService.getAllCategories().map { convertDto(it) }

    /**
     * 카테고리 수정 -> 수정된 제품을 포함한 카테고리 리스트 반환
     * PUT은 Multipart 폼을 지원X 그래서 POST로 수정 요청 쏴야 함
     */
    // 카테고리 수정
    @PostMapping("/{categoryId}")
    fun updateCategory(@PathVariable("categoryId") categoryId: Long,
                       @ModelAttribute form: CategoryRequest)
    = categoryService.updateCategory(categoryId, form).map { convertDto(it) }

    // 카테고리 삭제
    @DeleteMapping("/{categoryId}")
    fun deleteCategory(@PathVariable("categoryId") categoryId: Long)
    = categoryService.deleteCategory(categoryId)
}