package hntech.hntechserver.file

import hntech.hntechserver.config.*
import hntech.hntechserver.utils.logging.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
@Transactional
class FileService(private val fileRepository: FileRepository) {
    val log = logger()

    val baseFilePath = FILE_SAVE_PATH_WINDOW
//    val baseFilePath = FILE_SAVE_PATH_LINUX


    /**
     * 파일 생성(저장)
     */
    // 단일 파일 저장
    fun saveFile(
        file: MultipartFile,
        saveType: String = FILE_TYPE_DEFAULT
    ): File {
        if (file.isEmpty) throw FileException(FILE_IS_EMPTY)
        try {
            val originalFilename: String = file.originalFilename.toString()
            val extensionType: String = originalFilename.split(".")[1] // 파일 확장자 추출하기
            val serverFilename: String = UUID.randomUUID().toString() + ".$extensionType"
            val savedPath = when(saveType) {
                FILE_TYPE_ADMIN -> ADMIN_IMAGE_SAVE_PATH_WINDOW
                FILE_TYPE_DOCS -> DOCS_SAVE_PATH_WINDOW
                FILE_TYPE_IMAGE -> IMAGE_SAVE_PATH_WINDOW
                else -> FILE_SAVE_PATH_WINDOW
//                FILE_TYPE_ADMIN -> ADMIN_IMAGE_SAVE_PATH_LINUX
//                FILE_TYPE_DOCS -> DOCS_SAVE_PATH_LINUX
//                FILE_TYPE_IMAGE -> IMAGE_SAVE_PATH_LINUX
//                else -> FILE_SAVE_PATH_WINDOW
            } + serverFilename

            log.info("originFilename = {}, savedPath = {}", originalFilename, savedPath)

            // 서버 로컬 파일 스토리지에 해당 자료 저장
            file.transferTo(java.io.File(savedPath))

            return fileRepository.save(
                File(
                    originalFilename = originalFilename,
                    serverFilename = serverFilename,
                    savedPath = savedPath
                )
            )
        } catch (e: Exception) {
            log.warn(e.message)
            throw FileException(FILE_SAVING_ERROR)
        }
    }
    
    // 복수 파일 저장
    fun saveAllFiles(files: List<MultipartFile>) =
        files.map { saveFile(it) }.toMutableList()

    /**
     * 파일 조회
     */
    fun getFile(fileId: Long): File =
        fileRepository.findById(fileId).orElseThrow { FileException("$FILE_NOT_FOUND / ID : $fileId") }

    fun getFile(serverFilename: String): File =
        fileRepository.findByServerFilename(serverFilename) ?: throw FileException(FILE_NOT_FOUND)

    fun getFiles(idList: List<Long>) = idList.map { getFile(it) }.toMutableList()

    fun getSavedPath(serverFilename: String): String = baseFilePath + serverFilename

    fun getOriginalFilename(serverFilename: String): String = getFile(serverFilename).originalFilename

    /**
     * 파일 삭제
     */
    // 단일 파일 삭제
    fun deleteFile(file: File) = fileRepository.delete(file)
    fun deleteFile(fileId: Long) = fileRepository.deleteById(fileId)

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
    fun deleteFiles(idList: List<Long>) = idList.forEach { deleteFile(it) }

    /**
     * 파일 수정 (업데이트 : 기존파일 삭제 후 새로운 파일 저장)
     */
    // 단일 파일 수정
    fun updateFile(
        oldFile: File,
        newFile: MultipartFile,
        saveType: String = FILE_TYPE_DEFAULT,
    ): File {
        deleteFile(oldFile)
        return saveFile(newFile, saveType)
    }
}