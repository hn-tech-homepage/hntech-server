package hntech.hntechserver.file

import hntech.hntechserver.archive.Archive
import hntech.hntechserver.category.Category
import hntech.hntechserver.product.Product
import hntech.hntechserver.utils.FILE_SAVE_PATH_WINDOW_TEST
import hntech.hntechserver.utils.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

const val ARCHIVE_FILE = "archiveFile"
const val PRODUCT_FILE = "productFile"
const val DEFAULT_FILE = "defaultFile"

@Service
@Transactional
class FileService(
    private val fileRepository: FileRepository,
    private val archiveFileRepository: ArchiveFileRepository,
    private val productFileRepository: ProductFileRepository,
    ) {
    val log = logger()
    val baseFilePath = FILE_SAVE_PATH_WINDOW_TEST // 나중에 EC2 띄우면 여기만 변경 (리눅스로)

    /**
     * 파일 생성(저장)
     */
    // 파일을 서버에 업로드하고 엔티티화 해서 반환
    fun uploadAndMakeEntity(
        file: MultipartFile,
        type: String = DEFAULT_FILE,
        archive: Archive? = null,
        product: Product? = null,
    ): Any {
        try {
            val originFilename: String = file.originalFilename.toString()
            val extensionType: String = originFilename.split(".")[1] // 파일 확장자 추출하기
            val serverFileName: String = UUID.randomUUID().toString() + ".$extensionType"
            val savedPath = baseFilePath + serverFileName

            log.info("originFilename = {}, savedPath = {}", originFilename, savedPath)

            // 서버 로컬 파일 스토리지에 해당 자료 저장
            file.transferTo(java.io.File(savedPath))

            return when(type) {
                ARCHIVE_FILE -> ArchiveFile(originFileName = originFilename, serverFileName = serverFileName, fileArchive = archive)
                PRODUCT_FILE -> ProductFile(originFileName = originFilename, serverFileName = serverFileName, fileProduct = product)
                else -> File(originFileName = originFilename, serverFileName = serverFileName)
            }

        } catch (e: Exception) {
            log.error(FILE_SAVING_ERROR)
            throw FileException(FILE_SAVING_ERROR)
        }
    }

    // 단일 파일 저장
    fun saveFile(file: MultipartFile): File =
        fileRepository.save(uploadAndMakeEntity(file) as File)


    // 복수 파일 저장
    fun saveAllFiles(files: List<MultipartFile>): MutableList<File> {
        val result: MutableList<File> = mutableListOf()
        files.forEach { result.add(uploadAndMakeEntity(it) as File) }
        fileRepository.saveAll(result)
        return result
    }

    // 자료실 파일 복수 저장
    fun saveAllFiles(files: List<MultipartFile>, archive: Archive): MutableList<ArchiveFile> {
        val archiveFiles: MutableList<ArchiveFile> = mutableListOf()
        files.forEach {
            archiveFiles.add(
                uploadAndMakeEntity(it, type = ARCHIVE_FILE, archive = archive) as ArchiveFile
            )
        }
        archiveFileRepository.saveAll(archiveFiles)
        return archiveFiles
    }

    // 제품 파일 복수 저장
    fun saveAllFiles(files: List<MultipartFile>, product: Product): MutableList<ProductFile> {
        // 해당 제품에 대해 서버에 저장된 기존 파일 삭제
        deleteAllFiles(product.files)
        val productFiles: MutableList<ProductFile> = mutableListOf()
        files.forEach {
            productFiles.add(
                uploadAndMakeEntity(it, type = PRODUCT_FILE, product = product) as ProductFile
            )
        }
        productFileRepository.saveAll(productFiles)
        return productFiles
    }

    /**
     * 파일 삭제
     */
    // 단일 파일 삭제
    fun deleteFile(file: File) {
        val savedPath = baseFilePath + file.serverFileName
        val targetFile = java.io.File(savedPath)
        if (targetFile.exists()) {
            targetFile.delete()
            when(file) {
                is ArchiveFile -> archiveFileRepository.delete(file)
                is ProductFile -> productFileRepository.delete(file)
                else -> fileRepository.delete(file)
            }
        }
    }

    // 파일 전체 삭제
    fun <T> deleteAllFiles(files: MutableList<T>, type: String = DEFAULT_FILE) {
        when (type) {
            ARCHIVE_FILE -> files.forEach { deleteFile(it as ArchiveFile) }
            PRODUCT_FILE -> files.forEach { deleteFile(it as ProductFile) }
            else -> files.forEach { deleteFile(it as File) }
        }
    }


    // 카테고리 파일 전체 삭제
    fun deleteAllFiles(category: Category) {
        category.archives.forEach { deleteAllFiles(it) }
        category.products.forEach { deleteAllFiles(it) }
    }

    // 제품 관련 파일 전체 삭제
    fun deleteAllFiles(product: Product) = deleteAllFiles(product.files, type = PRODUCT_FILE)
    
    // 자료실 관련 파일 전제 삭제
    fun deleteAllFiles(archive: Archive) = deleteAllFiles(archive.files, type = ARCHIVE_FILE)


    /**
     * 파일 수정 (업데이트 : 기존파일 삭제 후 새로운 파일 저장)
     */
    // 단일 파일 수정
    fun updateFile(fileEntity: File, file: MultipartFile): File {
        deleteFile(fileEntity)
        return when(fileEntity) {
            is ArchiveFile -> uploadAndMakeEntity(file) as ArchiveFile
            is ProductFile -> uploadAndMakeEntity(file) as ProductFile
            else -> uploadAndMakeEntity(file) as File
        }
    }
}