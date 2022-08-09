package hntech.hntechserver.category

import hntech.hntechserver.file.FileResponse
import hntech.hntechserver.file.convertDto
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Positive

data class CreateCategoryForm(
    @field:NotBlank
    var categoryName: String,

    @field:Positive
    var image: Long? = null,
)

data class UpdateCategoryForm(
    @field:NotBlank
    var categoryName: String,

    @field:Positive
    var image: Long?,
    var showInMain: Boolean,
)

data class CategoryResponse(
    var id: Long,
    var categoryName: String,
    var sequence: Int,
    var image: FileResponse
) {
    constructor(category: Category): this(
        id = category.id!!,
        categoryName = category.categoryName,
        sequence = category.sequence,
        image = convertDto(category.file!!)
    )
}

data class CategoryListResponse(
    var categories: List<CategoryResponse>
)

fun convertDto(c: Category): CategoryResponse =
    CategoryResponse(
        id = c.id!!,
        categoryName = c.categoryName,
        sequence = c.sequence,
        image = convertDto(c.file!!)
    )

fun convertDto(categories: List<Category>): CategoryListResponse {
    return CategoryListResponse(categories.map { convertDto(it) })
}
