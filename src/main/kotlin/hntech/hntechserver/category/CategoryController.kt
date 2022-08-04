package hntech.hntechserver.category

import hntech.hntechserver.utils.error.ValidationException
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/category")
class CategoryController(private val categoryService: CategoryService) {

    /**
     * 관리자
     */
    // 카테고리 생성 : form-data 방식으로 file value에는 이미지를, text value에는 이름을 넣는다.
    @PostMapping
    fun createCategory(@Validated @ModelAttribute form: CategoryCreateForm,
                       bindingResult: BindingResult
    ): CategoryResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return convertDto(categoryService.createCategory(form))
    }

    // 카테고리 수정
    @PutMapping("/{categoryId}")
    fun updateCategory(@PathVariable("categoryId") categoryId: Long,
                       @Validated @ModelAttribute form: CategoryUpdateForm,
                       bindingResult: BindingResult
    ): CategoryListResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return convertDto(categoryService.updateCategory(categoryId, form))
    }

    // 카테고리 순서 변경
    /**
     * 카테고리를 맨 뒤로 옮길 때에는 targetCategoryId를 0으로 요청
     */
    @PatchMapping
    fun updateCategorySequence(@RequestParam("categoryId") categoryId: Long,
                               @RequestParam("targetCategoryId") targetCategoryId: Long,
    ): CategoryListResponse {
        return convertDto(categoryService.updateCategorySequence(categoryId, targetCategoryId))
    }

    // 카테고리 삭제
    @DeleteMapping("/{categoryId}")
    fun deleteCategory(@PathVariable("categoryId") categoryId: Long) =
        categoryService.deleteCategory(categoryId)

    /**
     * 클라이언트
     */
    // 카테고리 전체 조회
    @GetMapping
    fun getAllCategories(): CategoryListResponse =
        convertDto(categoryService.getAllCategories())
    
    // 메인에 보여질 카테고리 조회 (최대 8개)
    @GetMapping("/main")
    fun getMainCategories(): CategoryListResponse =
        convertDto(categoryService.getMainCategories())
}