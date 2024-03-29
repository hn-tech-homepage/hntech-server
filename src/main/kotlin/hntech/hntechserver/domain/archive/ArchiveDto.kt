package hntech.hntechserver.domain.archive

import hntech.hntechserver.common.*
import hntech.hntechserver.config.FILE_TYPE_ARCHIVE_ATTACHMENT
import hntech.hntechserver.config.FILE_TYPE_ARCHIVE_CONTENT_IMAGE
import hntech.hntechserver.domain.file.FileResponse
import hntech.hntechserver.utils.function.isNewCheck
import org.springframework.data.domain.Page
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size


data class ArchiveForm(
    @field:Size(max = MAX_TITLE_LENGTH, message = MAX_TITLE_LENGTH_MSG)
    var title: String,

    var categoryName: String,

    @field:Pattern(regexp = REG_BOOL, message = REG_BOOL_MSG)
    var notice: String,

    @field:Size(max = MAX_CONTENT_LENGTH, message = MAX_CONTENT_LENGTH_MSG)
    var content: String,

    var contentImageFiles: List<MultipartFile>?,

    var attachedFiles: List<MultipartFile>?
)

data class ArchiveDetailResponse(
    var id: Long,
    var categoryName: String,
    var notice: String,
    var title: String,
    var content: String,
    var createTime: String,
    var contentImageFiles: List<FileResponse>,
    var attachedFiles: List<FileResponse>
) {
    constructor(a: Archive) : this(
        id = a.id!!,
        categoryName = a.category?.categoryName ?: UNKNOWN,
        notice = a.notice,
        title = a.title,
        content = a.content,
        createTime = a.updateTime,
        contentImageFiles = a.files
            .filter { it.type == FILE_TYPE_ARCHIVE_CONTENT_IMAGE }
            .map { FileResponse(it) },
        attachedFiles = a.files
            .filter { it.type == FILE_TYPE_ARCHIVE_ATTACHMENT }
            .map { FileResponse(it) }
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
    var totalPages: Int,
    var totalElements: Long,
    var archives: List<ArchiveSimpleResponse>
) {
    constructor(archives: Page<Archive>) : this(
        currentPage = archives.number,
        totalPages = archives.totalPages,
        totalElements = archives.totalElements,
        archives = archives.map { ArchiveSimpleResponse(it) }.toList()
    )
}

data class ArchiveNoticeResponse(
    var notices: List<ArchiveSimpleResponse>
)