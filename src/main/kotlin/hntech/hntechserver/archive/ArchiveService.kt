package hntech.hntechserver.archive

import hntech.hntechserver.category.CategoryService
import hntech.hntechserver.config.MAX_NOTICE_NUM
import hntech.hntechserver.file.FileService
import hntech.hntechserver.utils.logging.logger
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ArchiveService(
    private val archiveRepository: ArchiveRepository,
    private val categoryService: CategoryService,
    private val fileService: FileService,
    ) {
    val log = logger()

    private fun checkNoticeable() {
        if (archiveRepository.countNotice() > MAX_NOTICE_NUM)
            throw ArchiveException(OVER_NOTICE_MAX_NUM)
    }

    /**
     * 자료실 글 생성
     */
    @Transactional
    fun createArchive(form: ArchiveForm): Archive {
        // 카테고리 가져오기
        val category = categoryService.getCategory(form.categoryName)

        // 공지사항 최대 개수 체크
        checkNoticeable()

        // 자료실 글 생성
        val archive = archiveRepository.save(Archive(
            title = form.title,
            notice = form.notice,
            content = form.content,
            category = category
        ))

        // 파일 지정하기 (파일이 없으면 수행 X)
        if (form.files.isNotEmpty()) {
            val files = form.files.map { fileService.getFile(it) }.toMutableList()
            archive.update(files = files)
        }

        return archive
    }

    /**
     * 자료 조회
     */
    // id 로 자료실 하나 조회
    fun getArchive(id: Long): Archive =
        archiveRepository.findById(id).orElseThrow { ArchiveException(ARCHIVE_NOT_FOUND) }

    // 자료 목록 조회 (페이징)
    fun getArchives(pageable: Pageable): Page<Archive> =
        archiveRepository.findAll(pageable)

    // 공지사항 모두 조회 (페이징)
    fun getAllNotice(): List<Archive> =
        archiveRepository.findAllNotice()

    /**
     * 자료 수정
     */
    @Transactional
    fun updateArchive(id: Long, form: ArchiveForm): Archive {
        val archive = getArchive(id)

        // 카테고리 가져오기
        val category = categoryService.getCategory(form.categoryName)

        // 공지사항 최대 개수 체크
        checkNoticeable()

        // 기존 오래된 파일들은 삭제
        fileService.deleteAllFiles(archive.files)
        val files = form.files.map { fileService.getFile(it) }.toMutableList()

        archive.update(
            notice = form.notice,
            title = form.title,
            content = form.content,
            category = category,
            files = files
        )

        return archive
    }

    /**
     * 자료 삭제
     */
    @Transactional
    fun deleteArchive(id: Long): Boolean {
        val targetArchive = getArchive(id)
        fileService.deleteAllFiles(targetArchive.files)
        archiveRepository.deleteById(id)
        return true
    }
}