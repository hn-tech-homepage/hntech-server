package hntech.hntechserver.archive

import org.springframework.data.domain.Page

data class ArchiveForm(
    var title: String,
    var productCategoryName: String,
    var archiveCategoryName: String,
    var isNotice: String,
    var content: String,
    var files: List<String>, // serverFilename
)

data class ArchiveSimpleResponse(
    var id: String,
    var productCategoryName: String,
    var archiveCategoryName: String,
    var isNotice: String,
    var title: String,
    var content: String,
    var createTime: String,
) {
    constructor(a: Archive) : this(
        id = a.id.toString(),
        productCategoryName = a.productCategory!!.categoryName,
        archiveCategoryName = a.archiveCategory!!.categoryName,
        isNotice = a.isNotice,
        title = a.title,
        content = a.content,
        createTime = a.createTime.toString()
        )
}

data class ArchiveDetailResponse(
    var title: String,
    var createdDate: String,
    var content: String,
    var files: List<String>,
)

data class ArchiveListResponse(
    var archives: List<ArchiveSimpleResponse>
) {
    constructor(archives: Page<Archive>) : this(archives.map { ArchiveSimpleResponse(it) }.toList())
}