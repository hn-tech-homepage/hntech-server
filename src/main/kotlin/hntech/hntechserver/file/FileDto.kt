package hntech.hntechserver.file

data class FileResponse(
    var id: Long? = null,
    var originFileName: String?
)

fun convertDto(f: File): FileResponse =
    FileResponse(
        id = f.id,
        originFileName = f.originFileName
    )