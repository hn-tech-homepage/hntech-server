package hntech.hntechserver.product

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/product")
class ProductController(private val productService: ProductService) {

    @PostMapping
    fun createItem(@ModelAttribute form: ProductCreateForm): ProductResponseForm
    = convertDto(productService.createProduct(form))

    @GetMapping
    fun getAllProducts(): List<ProductResponseForm>
    = productService.getAllProducts().map { convertDto(it) }

    @GetMapping("/{product_id}")
    fun getProduct(@PathVariable("product_id") id: Long): ProductResponseForm
    = convertDto(productService.getProduct(id))

    @PutMapping("/{product_id}")
    fun updateProduct(@PathVariable("product_id") id: Long,
                      @ModelAttribute form: ProductUpdateForm
    ): ProductResponseForm
    = convertDto(productService.updateProduct(id, form))

    @DeleteMapping("/{product_id}")
    fun deleteProduct(@PathVariable("product_id") id: Long)
    = productService.deleteProduct(id)
}