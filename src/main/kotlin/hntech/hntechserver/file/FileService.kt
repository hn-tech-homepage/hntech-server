package hntech.hntechserver.file

import hntech.hntechserver.utils.config.FILE_SAVE_PATH_LINUX
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

//    val baseFilePath = FILE_SAVE_PATH_WINDOW_TEST
    val baseFilePath = FILE_SAVE_PATH_LINUX

    /**
     * 파일 생성(저장)
     */
    // 단일 파일 저장
    fun saveFile(file: MultipartFile): File {
        if (file.isEmpty) throw FileException(FILE_IS_EMPTY)
        try {
            val originalFilename: String = file.originalFilename.toString()
            val extensionType: String = originalFilename.split(".")[1] // 파일 확장자 추출하기
            val serverFilename: String = UUID.randomUUID().toString() + ".$extensionType"
            val savedPath = baseFilePath + serverFilename

            log.info("originFilename = {}, savedPath = {}", originalFilename, savedPath)

            // 서버 로컬 파일 스토리지에 해당 자료 저장
            file.transferTo(java.io.File(savedPath))

            return fileRepository.save(
                File(
                    originalFilename = originalFilename,
                    serverFilename = serverFilename
                )
            )
        } catch (e: Exception) {
            log.error(e.message)
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
     * 파일 조회
     */
//    fun getFile(serverFilename: String): File =
//        fileRepository.findByServerFilename(serverFilename) ?: throw FileException(FILE_NOT_FOUND)
    fun getFile(fileId: Long): File =
        fileRepository.findById(fileId).orElseThrow { FileException(FILE_NOT_FOUND + " / 아이디 : $fileId") }
    fun getFiles(idList: List<Long>): MutableList<File> {
        val result: MutableList<File> = mutableListOf()
        idList.forEach { result.add(getFile(it)) }
        return result
    }
    fun getSavedPath(serverFilename: String): String = baseFilePath + serverFilename
    fun getOriginalFilename(serverFilename: String): String = fileRepository.findByServerFilename(serverFilename)!!.originalFilename
    fun getFile(serverFilename: String): File = fileRepository.findByServerFilename(serverFilename)!!

    /**
     * 파일 삭제
     */
    // 단일 파일 삭제
    fun deleteFile(file: File): Boolean {
        return try {
            val savedPath = baseFilePath + file.serverFilename
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

    // 단일 파일 삭제
    fun deleteFile(fileId: Long): Boolean {
        return try {
            val file = fileRepository.findById(fileId).get()
            val targetFile = java.io.File(baseFilePath + file.serverFilename)
            if (targetFile.exists()) {
                targetFile.delete()
                fileRepository.delete(file)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // 스토리지의 파일 삭제 (디비는 안건드림)
    fun deleteFile(serverFilename: String): Boolean {
        val targetFile = java.io.File(baseFilePath + serverFilename)
        return try {
            if (targetFile.exists()) targetFile.delete()
            true
        } catch (e: Exception) {
            false
        }
    }

    // 복수 파일 삭제
    fun deleteAllFiles(files: MutableList<File>) = files.forEach { deleteFile(it) }

    // 복수 파일 삭제
    fun deleteFiles(idList: List<Long>) =
        idList.forEach { deleteFile(it) }

    /**
     * 파일 수정 (업데이트 : 기존파일 삭제 후 새로운 파일 저장)
     */
    // 단일 파일 수정
    fun updateFile(oldFile: File, newFile: MultipartFile, entity: Any? = null, fileType: String? = null): File {
        deleteFile(oldFile)
        return saveFile(newFile)
    }

    // 복수 파일 수정
    fun updateFiles(oldFiles: List<File>, newFiles: List<MultipartFile>, entity: Any? = null, fileType: String? = null): List<File> {
        deleteAllFiles(oldFiles as MutableList<File>)
        return saveAllFiles(newFiles)
    }
}