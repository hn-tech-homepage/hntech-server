package hntech.hntechserver.category

import hntech.hntechserver.file.FileResponse
import hntech.hntechserver.file.convertDto
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CategoryRequest(
    @field:NotBlank
    var categoryName: String,

    var image: MultipartFile
)

data class CategoryResponse(
    var id: Long,
    var categoryName: String,
    var file: FileResponse
)

data class CategoryListResponse(
    var categories: List<CategoryResponse>
)

fun convertDto(c: Category): CategoryResponse =
    CategoryResponse(
        id = c.id!!,
        categoryName = c.categoryName,
        file = convertDto(c.file!!)
    )

fun convertDto(categories: List<Category>): CategoryListResponse {
    return CategoryListResponse(categories.map { convertDto(it) })
}