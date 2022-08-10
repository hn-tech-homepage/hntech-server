package hntech.hntechserver.product

import hntech.hntechserver.file.FileService
import hntech.hntechserver.utils.exception.ValidationException
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
     * 클라이언트
     */
    @GetMapping
    fun getAllProducts(@RequestParam(name = "category", required = false) categoryName: String?): ProductListResponse =
        ProductListResponse(productService.getAllProducts(categoryName).map { ProductSimpleResponse(it) })

    @GetMapping("/{product_id}")
    fun getProduct(@PathVariable("product_id") id: Long): ProductDetailResponse =
        ProductDetailResponse(productService.getProduct(id))

    /**
     * 관리자
     */
    @PostMapping
    fun createProduct(@Valid @RequestBody form: ProductCreateForm,
                      br: BindingResult
    ): ProductDetailResponse {
        if (br.hasErrors()) {
            fileService.deleteFiles(form.files.getFileIds())
            throw ValidationException(br)
        }
        return ProductDetailResponse(productService.createProduct(form))
    }

    @PutMapping("/{product_id}")
    fun updateProduct(@PathVariable("product_id") id: Long,
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
    @PatchMapping
    fun updateCategorySequence(@RequestParam("productId") productId: Long,
                               @RequestParam("targetProductId") targetProductId: Long,
    ): ProductListResponse {
        return ProductListResponse(
            productService.updateProductSequence(productId, targetProductId).map { ProductSimpleResponse(it) }
        )
    }

    @DeleteMapping("/{product_id}")
    fun deleteProduct(@PathVariable("product_id") id: Long) =
        productService.deleteProduct(id)
}