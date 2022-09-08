package hntech.hntechserver.domain.category

import hntech.hntechserver.common.REG_BOOL
import hntech.hntechserver.common.REG_BOOL_MSG
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class CreateCategoryForm(
    @field:NotBlank
    var categoryName: String,

    @field:Pattern(regexp = "^(archive|product)$", message = "archive 또는 product로 입력 가능합니다.")
    var type: String = "archive",

    var image: MultipartFile? = null,

    @field:Pattern(regexp = REG_BOOL, message = REG_BOOL_MSG)
    var showInMain: String = "false"
)

data class UpdateCategoryForm(
    @field:NotBlank
    var categoryName: String,

    var image: MultipartFile? = null,

    @field:Pattern(regexp = REG_BOOL, message = REG_BOOL_MSG)
    var showInMain: String = "false",
)

data class UpdateCategorySequenceForm(
    var currentCategoryId: Long,
    var targetCategoryId: Long,
)

data class ProductCategoryResponse(
    var id: Long,
    var categoryName: String,
    var imageServerFilename: String? = "",
    var imageOriginalFilename: String? = "",
    var showInMain: String = "false"
) {
    constructor(category: Category): this(
        id = category.id!!,
        categoryName = category.categoryName,
        imageOriginalFilename = category.file?.originalFilename,
        imageServerFilename = category.file?.serverFilename,
        showInMain = category.showInMain
    )
}

data class ArchiveCategoryResponse(
    var id: Long,
    var categoryName: String,
    var showInMain: String,
) {
    constructor(category: Category): this(
        id = category.id!!,
        categoryName = category.categoryName,
        showInMain = category.showInMain
    )
}

data class ProductCategoryListResponse(
    var categories: List<ProductCategoryResponse>
)

data class ArchiveCategoryListResponse(
    var categories: List<ArchiveCategoryResponse>
)

data class AllCategoryListResponse(
    var categories: List<ArchiveCategoryResponse>
)
