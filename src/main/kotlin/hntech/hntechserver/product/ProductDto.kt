package hntech.hntechserver.product

import hntech.hntechserver.category.Category
import hntech.hntechserver.file.FileResponse
import hntech.hntechserver.file.convertDto
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.NotBlank

data class ProductCreateForm(
    @field:NotBlank
    var categoryName: String,

    @field:NotBlank
    var productName: String,

    var description: String,
    var files: List<MultipartFile>
)

data class ProductUpdateForm(
    @field:NotBlank
    var productName: String,

    var description: String,
    var files: List<MultipartFile>
)

data class ProductResponse(
    var id: Long?,
    var category: String,
    var productName: String,
    var description: String,
    var files: List<FileResponse>
)

data class ProductListResponse(
    var products: List<ProductResponse>
)

fun convertEntity(form: ProductCreateForm, category: Category): Product {
    return Product(
        productCategory =  category,
        productName = form.productName,
        description = form.description
    )
}

fun convertDto(product: Product): ProductResponse {
    return ProductResponse(
        id = product.id,
        category = product.productCategory.categoryName,
        productName = product.productName,
        description = product.description,
        files = product.files.map { convertDto(it) }
    )
}

fun convertDto(products: List<Product>): ProductListResponse {
    return ProductListResponse(products.map { convertDto(it) })
}