package hntech.hntechserver.archive

import hntech.hntechserver.config.UNKNOWN
import hntech.hntechserver.file.FileResponse
import hntech.hntechserver.utils.function.isNewCheck
import org.springframework.data.domain.Page

data class ArchiveForm(
    var title: String,
    var categoryName: String,
    var notice: String,
    var content: String,
    var files: List<String>, // serverFilename
)

data class ArchiveDetailResponse(
    var id: Long,
    var categoryName: String,
    var title: String,
    var content: String,
    var createTime: String,
    var files: List<FileResponse>
) {
    constructor(a: Archive) : this(
        id = a.id!!,
        categoryName = a.category?.categoryName ?: UNKNOWN,
        title = a.title,
        content = a.content,
        createTime = a.updateTime,
        files = a.files.map { FileResponse(it) }
    )
}

data class ArchiveSimpleResponse(
    var id: Long,
    var categoryName: String,
    var title: String,
    var new: String = "false",
    var createTime: String,
) {
    constructor(a: Archive) : this(
        id = a.id!!,
        categoryName = a.category?.categoryName ?: UNKNOWN,
        new = isNewCheck(a.updateTime),
        title = a.title,
        createTime = a.updateTime
    )
}

data class ArchivePagedResponse(
    var currentPage: Int,
    var totalPage: Int,
    var archives: List<ArchiveSimpleResponse>
) {
    constructor(archives: Page<Archive>) : this(
        currentPage = archives.number,
        totalPage = archives.totalPages,
        archives = archives.map { ArchiveSimpleResponse(it) }.toList()
    )
}

data class ArchiveNoticeResponse(
    var notices: List<ArchiveSimpleResponse>
)