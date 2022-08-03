package hntech.hntechserver.file

import hntech.hntechserver.utils.config.FILE_SAVE_PATH_WINDOW_TEST
import hntech.hntechserver.utils.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
@Transactional
class FileService(private val fileRepository: FileRepository) {
    val log = logger()
    val baseFilePath = FILE_SAVE_PATH_WINDOW_TEST // 나중에 EC2 띄우면 여기만 변경 (리눅스로)

    /**
     * 파일 생성(저장)
     */
    // 단일 파일 저장
    fun saveFile(file: MultipartFile): File {
        try {
            val originFilename: String = file.originalFilename.toString()
            val extensionType: String = originFilename.split(".")[1] // 파일 확장자 추출하기
            val serverFileName: String = UUID.randomUUID().toString() + ".$extensionType"
            val savedPath = baseFilePath + serverFileName

            log.info("originFilename = {}, savedPath = {}", originFilename, savedPath)

            // 서버 로컬 파일 스토리지에 해당 자료 저장
            file.transferTo(java.io.File(savedPath))

            return fileRepository.save(File(originFileName = originFilename, serverFileName = serverFileName))
            
        } catch (e: Exception) {
            log.error(FILE_SAVING_ERROR)
            throw FileException(FILE_SAVING_ERROR)
        }
    }
    
    // 복수 파일 저장
    fun saveAllFiles(files: List<MultipartFile>): MutableList<File> {
        val result: MutableList<File> = mutableListOf()
        files.forEach { result.add(saveFile(it)) }
        return result
    }

    /**
     * 파일 삭제
     */
    // 단일 파일 삭제
    fun deleteFile(file: File): Boolean {
        return try {
            val savedPath = baseFilePath + file.serverFileName
            val targetFile = java.io.File(savedPath)
            if (targetFile.exists()) {
                targetFile.delete()
                fileRepository.delete(file)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    // 복수 파일 삭제
    fun deleteAllFiles(files: MutableList<File>) = files.forEach { deleteFile(it) }

    /**
     * 파일 수정 (업데이트 : 기존파일 삭제 후 새로운 파일 저장)
     */
    // 단일 파일 수정
    fun updateFile(fileEntity: File, newFile: MultipartFile): File {
        deleteFile(fileEntity)
        return saveFile(newFile)
    }

}



