package hntech.hntechserver.domain.file

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

    /**
     * 파일 생성(저장)
     */
    // 단일 파일 저장
    fun saveFile(
        file: MultipartFile,
        saveType: String = FILE_TYPE_DEFAULT,
    ): File {
        if (file.isEmpty) throw FileException(FILE_IS_EMPTY)
        try {
            val originalFilename: String = file.originalFilename.toString()
            val extensionType: String = originalFilename.split(".").let { it[it.size - 1] } // 파일 확장자 추출하기
            val serverFilename: String = UUID.randomUUID().toString() + ".$extensionType"
            val savedPath = when(saveType) {
                FILE_TYPE_ADMIN -> ADMIN_SAVE_PATH
                FILE_TYPE_CATEGORY -> CATEGORY_SAVE_PATH
                FILE_TYPE_PRODUCT -> PRODUCT_SAVE_PATH
                FILE_TYPE_ARCHIVE_ATTACHMENT -> ARCHIVE_SAVE_PATH
                FILE_TYPE_ARCHIVE_CONTENT_IMAGE -> ARCHIVE_SAVE_PATH
                FILE_TYPE_QUESTION_CONTENT_IMAGE -> QUESTION_SAVE_PATH
                else -> FILE_SAVE_PATH
            } + serverFilename

            log.info("originFilename = {}, savedPath = {}", originalFilename, savedPath)

            // 서버 로컬 파일 스토리지에 해당 자료 저장
            file.transferTo(java.io.File(savedPath))

            return fileRepository.save(
                File(
                    originalFilename = originalFilename,
                    serverFilename = serverFilename,
                    savedPath = savedPath,
                    type = saveType
                )
            )
        } catch (e: Exception) {
            log.warn(e.message)
            throw FileException(FILE_SAVING_ERROR)
        }
    }
    
    // 복수 파일 저장
    fun saveAllFiles(type: String, files: List<MultipartFile>) =
        files.map { saveFile(it, type) }.toMutableList()

    /**
     * 파일 조회
     */
    fun getFile(fileId: Long): File =
        fileRepository.findById(fileId).orElseThrow { FileException("$FILE_NOT_FOUND / ID : $fileId") }

    fun getFile(serverFilename: String): File =
        fileRepository.findByServerFilename(serverFilename) ?: throw FileException("[$serverFilename] $FILE_NOT_FOUND")

    fun getOriginalFilename(serverFilename: String): String = getFile(serverFilename).originalFilename

    /**
     * 파일 삭제
     */
    // 단일 파일 삭제
    fun deleteFile(file: File) = fileRepository.delete(file)
    fun deleteFile(fileId: Long) = fileRepository.deleteById(fileId)
    fun deleteFile(serverFilename: String) =
        fileRepository.findByServerFilename(serverFilename)?.let { fileRepository.delete(it) }

    // 복수 파일 삭제
    fun deleteAllFiles(files: MutableList<File>) = files.forEach { deleteFile(it) }

    /**
     * 파일 수정 (업데이트 : 기존파일 삭제 후 새로운 파일 저장)
     */
    // 단일 파일 수정
    fun updateFile(
        oldFileServerFilename: String,
        newMultipartFile: MultipartFile,
        saveType: String = FILE_TYPE_DEFAULT,
    ): File {
        try { deleteFile(getFile(oldFileServerFilename)) } catch (_: Exception) {}
        return saveFile(newMultipartFile, saveType)
    }
}