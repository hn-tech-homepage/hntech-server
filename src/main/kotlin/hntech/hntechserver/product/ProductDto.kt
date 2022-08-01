package hntech.hntechserver.product

import hntech.hntechserver.category.Category
import org.springframework.web.multipart.MultipartFile

data class ProductCreateForm(
    var categoryName: String,
    var productName: String,
    var description: String,
    var files: List<MultipartFile>
)

data class ProductResponseForm(
    var id: Long?,
    var category: String,
    var productName: String,
    var description: String,
    var files: List<String>
)

data class ProductUpdateForm(
    var productName: String,
    var description: String,
    var files: List<MultipartFile>
)

fun convertEntity(form: ProductCreateForm, category: Category): Product {
    return Product(
        productCategory =  category,
        productName = form.productName,
        description = form.description
    )
}

fun convertDto(product: Product): ProductResponseForm {
    return ProductResponseForm(
        id = product.id,
        category = product.productCategory.categoryName,
        productName = product.productName,
        description = product.description,
        files = product.files.map { it.serverFileName }
    )
}