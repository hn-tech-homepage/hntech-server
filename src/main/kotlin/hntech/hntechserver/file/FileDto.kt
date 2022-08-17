package hntech.hntechserver.file

data class FileResponse(
    var id: Long,
    var originalFilename: String,
    var serverFilename: String,
    var savedPath: String,
    var type: String = "",
) {
    constructor(file: File): this(
        id = file.id!!,
        originalFilename = file.originalFilename,
        serverFilename = file.serverFilename,
        savedPath = file.savedPath,
        type = file.type
    )
}

data class FileListResponse(
    var files: List<FileResponse>,
)

