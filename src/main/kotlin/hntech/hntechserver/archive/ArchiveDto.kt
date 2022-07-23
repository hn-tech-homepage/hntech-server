package hntech.hntechserver.archive


//data class FileRequest(
//    var fileName: String,
//    var
//)

data class ArchiveRequest(
    var title: String,
    var archiveCategory: String,
    var itemType: String,
    var isNotice: String,
    var content: String,
    var filesWithOriginFilename: List<String>,
)

data class ArchiveSimpleResponse(
    var id: String,
    var archiveCategory: String,
    var itemType: String,
    var content: String,
    var creatTime: String,
) {
    companion object {
        @JvmStatic fun createDto(a: Archive, itemType: String): ArchiveSimpleResponse {
            return ArchiveSimpleResponse(
                id = a.id.toString(),
                archiveCategory = a.archiveCategory!!.categoryName,
                content = a.content,
                creatTime = a.createTime.toString(),
                itemType = itemType
            )
        }
    }
}

data class ArchiveDetailResponse(
    var title: String,
    var createdDate: String,
    var content: String,
    var files: List<String>,
)

data class ArchiveListResponse(
    var archives: List<ArchiveSimpleResponse>
)