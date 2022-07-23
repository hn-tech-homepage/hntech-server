package hntech.hntechserver.file

import hntech.hntechserver.FileUploadException
import hntech.hntechserver.archive.FileRepository
import hntech.hntechserver.domain.File
import hntech.hntechserver.utils.BoolResponse
import hntech.hntechserver.utils.logger
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/file")
class FileController(private val fileRepository: FileRepository) {
    val log = logger()
    private val FILE_PATH = "C:\\dev\\"

    @PostMapping("/upload")
    fun uploadFile(@ModelAttribute("file") files: List<MultipartFile>): FileListResponse {
        val result: MutableList<FileResponse> = mutableListOf()
        files.forEach {
            try {
                val originFilename: String = it.originalFilename.toString()
                val extensionType: String = originFilename.split(".")[1] // 파일 확장자 추출하기
                val serverFileName: String = UUID.randomUUID().toString() + ".$extensionType"
                val savedPath = FILE_PATH + serverFileName

                log.info("originFilename = {}, savedPath = {}", originFilename, savedPath)

                // 서버 로컬 파일 스토리지에 해당 자료 저장
                it.transferTo(java.io.File(savedPath))

                // 디비에 파일 정보 저장
                val file = File(originFileName = originFilename, serverFileName = serverFileName, savedPath = savedPath)
                fileRepository.save(file)
                result.add(FileResponse.createDto(file))

            } catch (e: Exception) {
                log.error("파일 업로드 오류")
                throw FileUploadException(e.message.toString())
            }
        }
        return FileListResponse(result)
    }


}