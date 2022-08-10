package hntech.hntechserver.archive

import hntech.hntechserver.utils.isNewCheck
import org.springframework.data.domain.Page

data class ArchiveForm(
    var title: String,
    var productCategoryName: String,
    var archiveCategoryName: String,
    var notice: String,
    var content: String,
    var files: List<String>, // serverFilename
)

data class ArchiveDetailResponse(
    var id: Long,
    var productCategoryName: String,
    var archiveCategoryName: String,
    var notice: String,
    var title: String,
    var content: String,
    var createTime: String,
) {
    constructor(a: Archive) : this(
        id = a.id!!,
        productCategoryName = a.productCategory!!.categoryName,
        archiveCategoryName = a.archiveCategory!!.categoryName,
        notice = a.notice,
        title = a.title,
        content = a.content,
        createTime = a.updateTime.split(" ")[0] // yyyy-MM-dd HH:mm:ss중 뒤에 시간 자르기
    )
}

data class ArchiveSimpleResponse(
    var id: Long,
    var productCategoryName: String,
    var archiveCategoryName: String,
    var title: String,
    var notice: String,
    var new: String = "false",
    var createTime: String,
) {
    constructor(a: Archive) : this(
        id = a.id!!,
        productCategoryName = a.productCategory!!.categoryName,
        archiveCategoryName = a.archiveCategory!!.categoryName,
        notice = a.notice,
        new = isNewCheck(a.updateTime),
        title = a.title,
        createTime = a.updateTime.split(" ")[0] // yyyy-MM-dd HH:mm:ss중 뒤에 시간 자르기
    )
}

data class ArchiveListResponse(
    var archives: List<ArchiveSimpleResponse>
) {
    constructor(archives: Page<Archive>) : this(archives.map { ArchiveSimpleResponse(it) }.toList())
}