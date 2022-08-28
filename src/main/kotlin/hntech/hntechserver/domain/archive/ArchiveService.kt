package hntech.hntechserver.domain.archive

import hntech.hntechserver.config.MAX_NOTICE_NUM
import hntech.hntechserver.domain.category.CategoryService
import hntech.hntechserver.domain.file.FileService
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
    private val qArchiveRepository: QArchiveRepository
    ) {
    val log = logger()

    private fun checkNoticeable() {
        if (archiveRepository.countNotice() > MAX_NOTICE_NUM)
            throw ArchiveException(OVER_NOTICE_MAX_NUM)
    }

    private fun setNewFiles(form: ArchiveForm, archive: Archive) {
        form.files?.let {
            val newSavedFiles = it.map { newFile ->
                val newSavedFileEntity = fileService.saveFile(newFile)
                newSavedFileEntity.fileArchive = archive
                newSavedFileEntity // return
            }.toMutableList()
            archive.files.addAll(newSavedFiles)
        }
    }

    /**
     * 자료실 글 생성
     */
    @Transactional
    fun createArchive(form: ArchiveForm): Archive {
        // 카테고리 가져오기
        val category = categoryService.getCategory(form.categoryName)

        // 공지사항 최대 개수 체크
        if (form.notice == "true") checkNoticeable()

        // 자료실 글 생성
        val archive = archiveRepository.save(Archive(
            title = form.title,
            notice = form.notice,
            content = form.content,
            category = category
        ))

        // 파일 저장 (파일이 없으면 수행 X)
        setNewFiles(form, archive)

        return archive
    }

    /**
     * 자료 조회
     */
    // id 로 자료실 하나 조회
    fun getArchive(id: Long): Archive =
        archiveRepository.findById(id).orElseThrow { ArchiveException(ARCHIVE_NOT_FOUND) }

    // 자료 목록 조회 (페이징)
    fun getArchives(pageable: Pageable, categoryName: String? = null, keyword: String? = null): Page<Archive> =
        qArchiveRepository.searchArchive(pageable, categoryName, keyword)

    // 공지사항 모두 조회 (페이징)
    fun getAllNotice(): List<Archive> = archiveRepository.findAllNotice()

    /**
     * 자료 수정
     */
//    @Transactional
//    fun updateArchive(id: Long, form: ArchiveForm): Archive {
//
//        val archive = getArchive(id)
//
//        // 카테고리 가져오기
//        val category = categoryService.getCategory(form.categoryName)
//
//        // 공지사항 최대 개수 체크
//        if (form.notice == "true") checkNoticeable()
//
//        // 파일 업데이트
//        fileService.deleteAllFiles(archive.files)
//        archive.files.clear()
//        val files = form.files.map {
//            val newFile = fileService.getFile(it)
//            newFile.fileArchive = archive
//            newFile
//        }.toMutableList()
//
//        archive.update(
//            notice = form.notice,
//            title = form.title,
//            content = form.content,
//            category = category,
//            files = files
//        )
//
//        return archive
//    }

    @Transactional
    fun updateArchive(id: Long, form: ArchiveForm): Archive {
        // 자료실 가져오기
        val archive = getArchive(id)

        // 카테고리 가져오기
        val category = categoryService.getCategory(form.categoryName)

        // 공지사항 최대 개수 체크
        if (form.notice == "true") checkNoticeable()

        // 파일 변경
        setNewFiles(form, archive)

        archive.update(
            notice = form.notice,
            title = form.title,
            content = form.content,
            category = category,
        )

        return archive
    }



    /**
     * 자료 삭제
     */
    @Transactional
    fun deleteArchive(id: Long): Boolean {
        archiveRepository.deleteById(id)
        return true
    }

    @Transactional
    fun deleteAttachedFile(archiveId: Long, fileId: Long): Boolean {
        val targetFile = fileService.getFile(fileId)
        getArchive(archiveId).files.remove(targetFile)
        fileService.deleteFile(targetFile)
        return true
    }
}