package hntech.hntechserver.file

data class FileResponse(
    var id: Long?,
    var originalFilename: String,
    var serverFilename: String,
    var type: String = ""
)

fun convertDto(file: File) = FileResponse(
    file.id,
    file.originalFilename,
    file.serverFilename,
    file.type
)

data class FileListResponse(
    var files: List<FileResponse>,
)

fun convertDto(files: MutableList<File>) = FileListResponse(files.map { convertDto(it) })

