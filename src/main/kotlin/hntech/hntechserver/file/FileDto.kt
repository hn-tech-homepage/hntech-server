package hntech.hntechserver.file

data class FileResponse(
    var id: Long? = null,
    var originFileName: String,
    var savedPath: String,
) {
    companion object {
        @JvmStatic fun createDto(f: File): FileResponse {
            return FileResponse(id = f.id, originFileName = f.originFileName, savedPath = f.savedPath)
        }
    }
}

data class FileListResponse(
    var files: List<FileResponse>,
)