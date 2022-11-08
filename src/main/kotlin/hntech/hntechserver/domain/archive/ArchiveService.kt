package hntech.hntechserver.domain.archive

import hntech.hntechserver.common.BoolResponse
import hntech.hntechserver.common.MAX_NOTICE_NUM
import hntech.hntechserver.config.FILE_TYPE_ARCHIVE_ATTACHMENT
import hntech.hntechserver.config.FILE_TYPE_ARCHIVE_CONTENT_IMAGE
import hntech.hntechserver.domain.category.CategoryService
import hntech.hntechserver.domain.file.FileService
import hntech.hntechserver.utils.logging.logger
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional(readOnly = true)
class ArchiveService(
    private val archiveRepository: ArchiveRepository,
    private val categoryService: CategoryService,
    private val fileService: FileService
    ) {
    val log = logger()

    /**
     * 편의 메서드
     */
    // Archive 엔티티 조회
    private fun getArchiveById(id: Long): Archive =
        archiveRepository.findById(id).orElseThrow { ArchiveException(ARCHIVE_NOT_FOUND) }

    // 공지사항 최대 갯수 체크
    private fun checkNoticeable() {
        if (archiveRepository.countNotice() >= MAX_NOTICE_NUM)
            throw ArchiveException(OVER_MAX_NOTICE_NUM)

    }

    // 자료실 내용 사진, 첨부 파일 저장
    private fun setNewFiles(files: List<MultipartFile>?, archive: Archive, type: String) {
        files?.let {
            val newSavedFiles = it.map { newFile ->
                val newSavedFileEntity = fileService.saveFile(newFile, type)
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
    fun createArchive(form: ArchiveForm): ArchiveDetailResponse {
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

        // 첨부 파일 저장 (파일이 없으면 수행 X)
        setNewFiles(form.attachedFiles, archive, FILE_TYPE_ARCHIVE_ATTACHMENT)

        // 내용 사진 저장 (파일이 없으면 수행 X)
        setNewFiles(form.contentImageFiles, archive, FILE_TYPE_ARCHIVE_CONTENT_IMAGE)

        return ArchiveDetailResponse(archive)
    }

    /**
     * 자료 조회
     */
    // id 로 자료실 하나 조회
    fun getArchive(id: Long) = ArchiveDetailResponse(
            archiveRepository.findById(id).orElseThrow { ArchiveException(ARCHIVE_NOT_FOUND) }
        )

    // 자료 목록 조회 (페이징)
    fun getArchives(
        pageable: Pageable, 
        categoryName: String? = null, 
        keyword: String? = null
    ) = ArchivePagedResponse(
        archiveRepository.searchArchive(pageable, categoryName, keyword)
    )
    
    // 공지사항 모두 조회 (페이징)
    fun getAllNotice() = ArchiveNoticeResponse(
        archiveRepository.findAllNotice().map { ArchiveSimpleResponse(it) }
    )

    /**
     * 자료 수정
     */
    @Transactional
    fun updateArchive(id: Long, form: ArchiveForm): ArchiveDetailResponse {
        // 자료실 가져오기
        val archive = getArchiveById(id)

        // 카테고리 가져오기
        val category = categoryService.getCategory(form.categoryName)

        // 공지사항 최대 개수 체크
        if (form.notice == "true") checkNoticeable()

        // 첨부 파일 변경 (파일이 없으면 수행 X)
        setNewFiles(form.attachedFiles, archive, FILE_TYPE_ARCHIVE_ATTACHMENT)

        // 내용 사진 변경 (파일이 없으면 수행 X)
        setNewFiles(form.contentImageFiles, archive, FILE_TYPE_ARCHIVE_CONTENT_IMAGE)

        archive.update(
            notice = form.notice,
            title = form.title,
            content = form.content,
            category = category,
        )

        return ArchiveDetailResponse(archive)
    }

    /**
     * 자료 삭제
     */
    @Transactional
    fun deleteArchive(id: Long): BoolResponse {
        archiveRepository.deleteById(id)
        return BoolResponse(true)
    }

    // 자료실 글에 올라가 있는 파일(내용 사진, 첨부 파일) 삭제
    @Transactional
    fun deleteArchiveFile(archiveId: Long, fileId: Long) = BoolResponse(
            getArchiveById(archiveId).files
            .remove(fileService.getFile(fileId))
        )
}