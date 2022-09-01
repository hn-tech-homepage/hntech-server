package hntech.hntechserver.domain.category

import hntech.hntechserver.utils.BoolResponse
import hntech.hntechserver.auth.Auth
import hntech.hntechserver.config.ARCHIVE
import hntech.hntechserver.config.PRODUCT
import hntech.hntechserver.domain.file.FileService
import hntech.hntechserver.exception.ValidationException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/category")
class CategoryController(private val categoryService: CategoryService) {
    /**
     * 사용자 모드
     */
    // 카테고리 전체 조회
    @GetMapping
    fun getAllCategories() =
        AllCategoryListResponse(categoryService.getAllCategories().map { ArchiveCategoryResponse(it) })


    // 제품 카테고리 전체 조회
    @GetMapping("/product")
    fun getAllProductCategories() =
        ProductCategoryListResponse(
            categoryService.getAllByType(PRODUCT).map { ProductCategoryResponse(it) }
        )

    // 자료실 카테고리 전체 조회
    @GetMapping("/archive")
    fun getAllArchiveCategories() =
        ArchiveCategoryListResponse(
            categoryService.getAllByType(ARCHIVE).map { ArchiveCategoryResponse(it) }
        )

    // 메인에 보여질 카테고리 조회 (최대 개수는 GlobalConfig 에 정의)
    @GetMapping("/main")
    fun getMainCategories() =
        ProductCategoryListResponse(
            categoryService.getMainCategories().map { ProductCategoryResponse(it) }
        )

    /**
     * 관리자 모드
     */
    // 카테고리 생성
    @Auth
    @PostMapping
    fun createCategory(
        @Valid @ModelAttribute form: CreateCategoryForm,
        br: BindingResult
    ): ProductCategoryResponse {
        if (br.hasErrors()) throw ValidationException(br)
        return ProductCategoryResponse(categoryService.createCategory(form))
    }

    // 카테고리 수정
    @Auth
    @PutMapping("/{categoryId}")
    fun updateCategory(
        @PathVariable("categoryId") id: Long,
        @Valid @ModelAttribute form: UpdateCategoryForm,
        br: BindingResult
    ): ProductCategoryListResponse {
        if (br.hasErrors()) throw ValidationException(br)
        return ProductCategoryListResponse(
            categoryService.updateCategory(id, form).map { ProductCategoryResponse(it) }
        )
    }

    // 카테고리 순서 변경
    // 맨 뒤로 옮길 때에는 targetCategoryId를 0으로 요청
    @Auth
    @PutMapping("/sequence")
    fun updateCategorySequence(
        @RequestBody form: UpdateCategorySequenceForm
    ) = ProductCategoryListResponse(
            categoryService.updateCategorySequence(form).map { ProductCategoryResponse(it) }
        )

    // 카테고리 삭제
    @Auth
    @DeleteMapping("/{categoryId}")
    fun deleteCategory(
        @PathVariable("categoryId") id: Long
    ) = BoolResponse(categoryService.deleteCategory(id))

    @Auth
    @DeleteMapping("/{categoryId}/file/{fileId}")
    fun deleteAttachedFile(@PathVariable("categoryId") categoryId: Long,
                           @PathVariable("fileId") fileId: Long
    ) = BoolResponse(categoryService.deleteAttachedFile(categoryId, fileId))
}