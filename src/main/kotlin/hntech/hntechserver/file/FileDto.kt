package hntech.hntechserver.file

fun createResponse(f: File) =
    FileResponse(
        id = f.id,
        originFileName = f.originFileName,
        savedPath = ""
    )

data class FileResponse(
    var id: Long? = null,
    var originFileName: String,
    var savedPath: String,
)

data class FileListResponse(
    var files: List<FileResponse>,
)