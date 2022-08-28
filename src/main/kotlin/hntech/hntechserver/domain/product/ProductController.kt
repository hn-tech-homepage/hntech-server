package hntech.hntechserver.domain.product

import hntech.hntechserver.domain.file.FileService
import hntech.hntechserver.auth.Auth
import hntech.hntechserver.exception.ValidationException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/product")
class ProductController(
    private val productService: ProductService,
    private val fileService: FileService
) {
    /**
     * 사용자 모드
     */
    @GetMapping
    fun getAllProducts(
        @RequestParam(name = "category", required = false) categoryName: String?
    ) = ProductListResponse(
            productService.getAllProducts(categoryName).map { ProductSimpleResponse(it) }
        )

    @GetMapping("/{productId}")
    fun getProduct(@PathVariable("productId") id: Long) =
        ProductDetailResponse(productService.getProduct(id))

    /**
     * 관리자 모드
     */
    @Auth
    @PostMapping
    fun createProduct(@Valid @RequestBody form: ProductCreateForm,
                      br: BindingResult
    ): ProductDetailResponse {
        if (br.hasErrors()) {
            form.files?.let { fileService.deleteFiles(it.getFileIds()) }
            throw ValidationException(br)
        }
        return ProductDetailResponse(productService.createProduct(form))
    }

    @Auth
    @PutMapping("/{productId}")
    fun updateProduct(@PathVariable("productId") id: Long,
                      @Valid @RequestBody form: ProductUpdateForm,
                      br: BindingResult
    ): ProductDetailResponse {
        if (br.hasErrors()) {
            fileService.deleteFiles(form.files.getFileIds())
            throw ValidationException(br)
        }
        return ProductDetailResponse(productService.updateProduct(id, form))
    }
    
    // 제품 순서 변경
    // 맨 뒤로 옮길 때에는 targetProductId를 0으로 요청
    @Auth
    @PatchMapping
    fun updateCategorySequence(@RequestParam("productId") productId: Long,
                               @RequestParam("targetProductId") targetProductId: Long,
    ): ProductListResponse {
        return ProductListResponse(
            productService.updateProductSequence(productId, targetProductId).map { ProductSimpleResponse(it) }
        )
    }

    @Auth
    @DeleteMapping("/{productId}")
    fun deleteProduct(@PathVariable("productId") id: Long) =
        productService.deleteProduct(id)
}