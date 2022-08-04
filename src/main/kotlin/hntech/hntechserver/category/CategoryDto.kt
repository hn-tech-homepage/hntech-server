package hntech.hntechserver.category

import hntech.hntechserver.file.FileResponse
import hntech.hntechserver.file.convertDto
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.NotBlank

data class CategoryCreateForm(
    @field:NotBlank
    var categoryName: String,

    var image: MultipartFile
)

data class CategoryUpdateForm(
    @field:NotBlank
    var categoryName: String,

    var image: MultipartFile,
    var showInMain: Boolean,
)

data class CategoryResponse(
    var id: Long,
    var categoryName: String,
    var sequence: Int,
    var file: FileResponse
)

data class CategoryListResponse(
    var categories: List<CategoryResponse>
)

fun convertDto(c: Category): CategoryResponse =
    CategoryResponse(
        id = c.id!!,
        categoryName = c.categoryName,
        sequence = c.sequence,
        file = convertDto(c.file!!)
    )

fun convertDto(categories: List<Category>): CategoryListResponse {
    return CategoryListResponse(categories.map { convertDto(it) })
}
