package hntech.hntechserver.file

import hntech.hntechserver.FileException
import hntech.hntechserver.archive.Archive
import hntech.hntechserver.archive.ArchiveRepository
import hntech.hntechserver.utils.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class FileService(
    private val fileRepository: FileRepository,
    private val archiveRepository: ArchiveRepository,

    ) {
    val log = logger()
    private val FILE_PATH = "C:\\dev\\"

    fun saveFile(file: MultipartFile): File {
        try {
            val originFilename: String = file.originalFilename.toString()
            val extensionType: String = originFilename.split(".")[1] // 파일 확장자 추출하기
            val serverFileName: String = UUID.randomUUID().toString() + ".$extensionType"
            val savedPath = FILE_PATH + serverFileName

            log.info("originFilename = {}, savedPath = {}", originFilename, savedPath)

            // 서버 로컬 파일 스토리지에 해당 자료 저장
            file.transferTo(java.io.File(savedPath))

//            // 디비에 파일 정보 저장
            return File(originFileName = originFilename, serverFileName = serverFileName, savedPath = savedPath)

        } catch (e: Exception) {
            log.error("파일 업로드 오류")
            throw FileException(e.message.toString())
        }
    }

    @Transactional
    fun saveArchiveFiles(files: List<MultipartFile>, archive: Archive): MutableList<ArchiveFile> {
        val archiveFiles: MutableList<ArchiveFile> = mutableListOf()

        files.forEach {
            val savedFile: File = saveFile(it)
            val dbArchiveFile = ArchiveFile(savedFile.originFileName, savedFile.serverFileName, savedFile.savedPath, archive)
            archiveFiles.add(dbArchiveFile)
            fileRepository.save(dbArchiveFile)
        }

        return archiveFiles
    }


    // 단일 업로드
    fun upload(file: MultipartFile) = createResponse(saveFile(file))

    // 여러개 업로드
    fun upload(files: List<MultipartFile>): FileListResponse {
        val result: MutableList<FileResponse> = mutableListOf()
        files.forEach {
            result.add(createResponse(saveFile(it)))
        }
        return FileListResponse(result)
    }


    // 파일 삭제
    @Transactional
    fun deleteFile(savedPath: String) {
        val targetFile = java.io.File(savedPath)
        if (targetFile.exists()) {
            targetFile.delete()
            fileRepository.deleteBySavedPath(savedPath)
        }
        else throw FileException("파일 삭제 실패")
    }

}