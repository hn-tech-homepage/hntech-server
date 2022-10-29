package hntech.hntechserver.domain.category

import hntech.hntechserver.auth.Auth
import hntech.hntechserver.common.BoolResponse
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/category")
class CategoryController(private val categoryService: CategoryService) {
    /**
     * 사용자 모드
     */
    // 카테고리 전체 조회
    @GetMapping
    fun getAllCategories(): AllCategoryListResponse =
        categoryService.getAllCategoriesToDto()

    // 제품 카테고리 전체 조회
    @GetMapping("/product")
    fun getAllProductCategories(): ProductCategoryListResponse =
        categoryService.getAllProductCategories()

    // 자료실 카테고리 전체 조회
    @GetMapping("/archive")
    fun getAllArchiveCategories(): ArchiveCategoryListResponse =
        categoryService.getAllArchiveCategories()

    // 메인에 보여질 카테고리 조회 (최대 개수는 GlobalConfig 에 정의)
    @GetMapping("/main")
    fun getMainCategories(): ProductCategoryListResponse =
        categoryService.getMainCategories()

    @GetMapping("/product/parent")
    fun getParentProductCategories(): ProductCategoryListResponse =
        categoryService.getParentProductCategories()

    @GetMapping("/product/{parent}/children")
    fun getChildrenProductCategories(@PathVariable("parent") parent: String): ProductCategoryListResponse =
        categoryService.getChildrenProductCategories(parent)

    /**
     * 관리자 모드
     */
    // 카테고리 생성
    @Auth
    @PostMapping
    fun createCategory(
        @Valid @ModelAttribute form: CreateCategoryForm
    ): ProductCategoryResponse = categoryService.createCategory(form)

    // 카테고리 수정
    @Auth
    @PutMapping("/{categoryId}")
    fun updateCategory(
        @PathVariable("categoryId") id: Long,
        @Valid @ModelAttribute form: UpdateCategoryForm
    ): ProductCategoryListResponse = categoryService.updateCategory(id, form)

    // 카테고리 순서 변경
    // 맨 앞으로 옮길 때에는 targetCategoryId를 0으로 요청
    @Auth
    @PutMapping("/sequence")
    fun updateCategorySequence(
        @RequestBody form: UpdateCategorySequenceForm
    ): ProductCategoryListResponse = categoryService.updateCategorySequence(form)


    // 카테고리 삭제
    @Auth
    @DeleteMapping("/{categoryId}")
    fun deleteCategory(
        @PathVariable("categoryId") id: Long
    ): BoolResponse = categoryService.deleteCategory(id)
}