package hntech.hntechserver.file

import hntech.hntechserver.FileUploadException
import hntech.hntechserver.utils.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class FileService(private val fileRepository: FileRepository) {
    val log = logger()
    private val FILE_PATH = "C:\\dev\\"

    @Transactional
    fun saveFile(file: MultipartFile): File {
        try {
            val originFilename: String = file.originalFilename.toString()
            val extensionType: String = originFilename.split(".")[1] // 파일 확장자 추출하기
            val serverFileName: String = UUID.randomUUID().toString() + ".$extensionType"
            val savedPath = FILE_PATH + serverFileName

            log.info("originFilename = {}, savedPath = {}", originFilename, savedPath)

            // 서버 로컬 파일 스토리지에 해당 자료 저장
            file.transferTo(java.io.File(savedPath))

            // 디비에 파일 정보 저장
            val dbFile = File(originFileName = originFilename, serverFileName = serverFileName, savedPath = savedPath)
            fileRepository.save(dbFile)
            return dbFile

        } catch (e: Exception) {
            log.error("파일 업로드 오류")
            throw FileUploadException(e.message.toString())
        }
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

}