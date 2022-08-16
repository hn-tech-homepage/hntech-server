package hntech.hntechserver.category

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Positive

data class CreateCategoryForm(
    @field:NotBlank
    var categoryName: String,

    @field:Pattern(regexp = "^(archive|product)$", message = "archive 또는 product로 입력 가능합니다.")
    var type: String = "archive",

    @field:Positive
    var imageFileId: Long? = null,
)

data class UpdateCategoryForm(
    @field:NotBlank
    var categoryName: String,

    @field:Positive
    var imageFileId: Long?,
    var showInMain: Boolean,
)

data class UpdateCategorySequenceForm(
    var currentCategoryId: Long,
    var targetCategoryId: Long,
)

data class ProductCategoryResponse(
    var id: Long,
    var categoryName: String,
    var sequence: Int,
    var imageServerFilename: String? = "",
) {
    constructor(category: Category): this(
        id = category.id!!,
        categoryName = category.categoryName,
        sequence = category.sequence,
        imageServerFilename = category.file?.serverFilename
    )
}

data class ArchiveCategoryResponse(
    var id: Long,
    var categoryName: String,
) {
    constructor(category: Category): this(
        id = category.id!!,
        categoryName = category.categoryName
    )
}

data class ProductCategoryListResponse(
    var categories: List<ProductCategoryResponse>
)

data class ArchiveCategoryListResponse(
    var categories: List<ArchiveCategoryResponse>
)

data class AllCategoryNameListResponse(
    var allCategoryNames: List<String>
)

fun convertDto(c: Category): ProductCategoryResponse =
    ProductCategoryResponse(
        id = c.id!!,
        categoryName = c.categoryName,
        sequence = c.sequence,
        imageServerFilename = c.file?.serverFilename
    )

fun convertDto(categories: List<Category>): ProductCategoryListResponse {
    return ProductCategoryListResponse(categories.map { convertDto(it) })
}
