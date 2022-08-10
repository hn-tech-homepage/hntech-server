package hntech.hntechserver.archive

import hntech.hntechserver.category.CategoryService
import hntech.hntechserver.file.FileService
import hntech.hntechserver.utils.logger
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ArchiveService(
    private val archiveRepository: ArchiveRepository,
    private val categoryService: CategoryService,
    private val fileService: FileService,
    ) {
    val log = logger()

    /**
     * 자료실 글 생성
     */
    fun createArchive(form: ArchiveForm): Archive {
        // 제품 카테고리 가져오기
        val productCategory = categoryService.getCategory(form.productCategoryName)

        // 자료실 카테고리 가져오기
        val archiveCategory = categoryService.getCategory(form.archiveCategoryName)

        // 자료실 글 생성
        val archive = archiveRepository.save(Archive(
            title = form.title,
            notice = form.notice,
            content = form.content,
            archiveCategory = archiveCategory,
            productCategory = productCategory
        ))

        // 파일 지정하기
        val files = form.files.map { fileService.getFile(it) }.toMutableList()
        archive.update(files = files)

        return archive
    }

    /**
     * 자료 조회
     */
    // id 로 자료실 하나 조회
    @Transactional(readOnly = true)
    fun getArchive(id: Long): Archive =
        archiveRepository.findById(id).orElseThrow { ArchiveException(ARCHIVE_NOT_FOUND) }

    // 자료 목록 조회 (페이징)
    @Transactional(readOnly = true)
    fun getArchives(pageable: Pageable): Page<Archive> =
        archiveRepository.findAll(pageable)

    /**
     * 자료 수정
     */
    fun updateArchive(id: Long, form: ArchiveForm): Archive {
        val archive = getArchive(id)

        // 제품 카테고리 가져오기
        val productCategory = categoryService.getCategory(form.productCategoryName)

        // 자료실 카테고리 가져오기
        val archiveCategory = categoryService.getCategory(form.archiveCategoryName)

        // 기존 오래된 파일들은 삭제
        fileService.deleteAllFiles(archive.files)
        val files = form.files.map { fileService.getFile(it) }.toMutableList()

        archive.update(
            notice = form.notice,
            title = form.title,
            content = form.content,
            archiveCategory = archiveCategory,
            productCategory = productCategory,
            files = files
        )

        return archive
    }

    /**
     * 자료 삭제
     */
    fun deleteArchive(id: Long): Boolean {
        val targetArchive = getArchive(id)
        fileService.deleteAllFiles(targetArchive.files)
        archiveRepository.deleteById(id)
        return true
    }
}