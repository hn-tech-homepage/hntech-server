package hntech.hntechserver.file

data class FileResponse(
    var originalFilename: String,
    var serverFilename: String,
)

fun convertDto(file: File) = FileResponse(file.originalFilename, file.serverFilename)

data class FileListResponse(
    var files: List<FileResponse>,
)

fun convertDto(files: MutableList<File>) = FileListResponse(files.map { convertDto(it) })

