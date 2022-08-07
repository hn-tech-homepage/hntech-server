package hntech.hntechserver.category

import hntech.hntechserver.file.FileService
import hntech.hntechserver.utils.error.ValidationException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/category")
class CategoryController(
    private val categoryService: CategoryService,
    private val fileService: FileService
) {
    /**
     * 클라이언트
     */
    // 카테고리 전체 조회
    @GetMapping
    fun getAllCategories(): CategoryListResponse =
        CategoryListResponse(
            categoryService.getAllCategories().map { CategoryResponse(it) }
        )

    // 메인에 보여질 카테고리 조회 (최대 개수는 GlobalConfig에 정의)
    @GetMapping("/main")
    fun getMainCategories(): CategoryListResponse =
        CategoryListResponse(
            categoryService.getMainCategories().map { CategoryResponse(it) }
        )

    /**
     * 관리자
     */
    @PostMapping
    fun createCategory(@Valid @RequestBody form: CreateCategoryForm,
                       br: BindingResult
    ): CategoryResponse {
        if (br.hasErrors()) {
            // 검증 실패 시 미리 업로드된 파일 삭제
            fileService.deleteFile(form.image!!)
            throw ValidationException(br)
        }
        return CategoryResponse(categoryService.createCategory(form))
    }

    // 카테고리 수정
    @PutMapping("/{categoryId}")
    fun updateCategory(@PathVariable("categoryId") categoryId: Long,
                       @Valid @RequestBody form: UpdateCategoryForm,
                       br: BindingResult
    ): CategoryListResponse {
        if (br.hasErrors()) {
            fileService.deleteFile(form.image!!)
            throw ValidationException(br)
        }
        return CategoryListResponse(
            categoryService.updateCategory(categoryId, form).map { CategoryResponse(it) }
        )
    }

    // 카테고리 순서 변경
    // 맨 뒤로 옮길 때에는 targetCategoryId를 0으로 요청
    @PatchMapping
    fun updateCategorySequence(@RequestParam("categoryId") categoryId: Long,
                               @RequestParam("targetCategoryId") targetCategoryId: Long,
    ): CategoryListResponse {
        return CategoryListResponse(
            categoryService.updateCategorySequence(categoryId, targetCategoryId).map { CategoryResponse(it) }
        )
    }

    // 카테고리 삭제
    @DeleteMapping("/{categoryId}")
    fun deleteCategory(@PathVariable("categoryId") categoryId: Long) =
        categoryService.deleteCategory(categoryId)
}