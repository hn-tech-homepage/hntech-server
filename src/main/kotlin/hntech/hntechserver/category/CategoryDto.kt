package hntech.hntechserver.category

import org.springframework.web.multipart.MultipartFile

data class CreateItemCategoryRequest(
    var categoryName: String,
    var image: MultipartFile,
)

data class CreateArchiveCategoryRequest(
    var categoryName: String,
)

data class ArchiveCategoryResponse(
    var categoryName: String,
)

data class ItemCategoryResponse(
    var categoryName: String,
    var imagePath: String,
)

data class ItemCategoryListResponse(
    var categories: List<ItemCategoryResponse>
)

data class ArchiveCategoryListResponse(
    var categories: List<ArchiveCategoryResponse>
)

fun convertItemDto(c: Category) = ItemCategoryResponse(categoryName = c.categoryName, imagePath = c.categoryImagePath)
fun convertArchiveDto(c: Category) = ArchiveCategoryResponse(categoryName = c.categoryName)
