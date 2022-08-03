package hntech.hntechserver.product

import hntech.hntechserver.utils.error.ValidationException
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/product")
class ProductController(private val productService: ProductService) {

    /**
     * 클라이언트
     */
    @GetMapping
    fun getAllProducts(@RequestParam(name = "category", required = false) categoryName: String?): ProductListResponse =
        convertDto(productService.getAllProducts(categoryName))

    @GetMapping("/{product_id}")
    fun getProduct(@PathVariable("product_id") id: Long): ProductResponse =
        convertDto(productService.getProduct(id))

    /**
     * 관리자
     */
    @PostMapping
    fun createItem(@Validated @ModelAttribute form: ProductCreateForm,
                   bindingResult: BindingResult
    ): ProductResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return convertDto(productService.createProduct(form))
    }

    @PutMapping("/{product_id}")
    fun updateProduct(@PathVariable("product_id") id: Long,
                      @Validated @ModelAttribute form: ProductUpdateForm,
                      bindingResult: BindingResult
    ): ProductResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return convertDto(productService.updateProduct(id, form))
    }

    @DeleteMapping("/{product_id}")
    fun deleteProduct(@PathVariable("product_id") id: Long) =
        productService.deleteProduct(id)
}