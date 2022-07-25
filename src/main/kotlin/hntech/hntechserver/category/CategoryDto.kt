package hntech.hntechserver.category

import org.springframework.web.multipart.MultipartFile

data class ItemCategoryRequest(
    var categoryName: String = "",
    var image: MultipartFile,
)

data class ArchiveCategoryRequest(
    var categoryName: String,
)

data class ArchiveCategoryResponse(
    var categoryId: Long,
    var categoryName: String,
)

data class ItemCategoryResponse(
    var categoryId: Long,
    var categoryName: String,
    var imagePath: String,
)

data class ItemCategoryListResponse(
    var categories: List<ItemCategoryResponse>
)

data class ArchiveCategoryListResponse(
    var categories: List<ArchiveCategoryResponse>
)

fun convertItemDto(c: Category) =
    ItemCategoryResponse(
        categoryId = c.id!!,
        categoryName = c.categoryName,
        imagePath = c.categoryImagePath
    )

fun convertArchiveDto(c: Category) =
    ArchiveCategoryResponse(
        categoryId = c.id!!,
        categoryName = c.categoryName
    )
