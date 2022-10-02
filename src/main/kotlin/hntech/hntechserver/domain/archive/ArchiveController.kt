package hntech.hntechserver.domain.archive

import hntech.hntechserver.auth.Auth
import hntech.hntechserver.common.BoolResponse
import hntech.hntechserver.common.PAGE_SIZE
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RestController
@RequestMapping("/archive")
class ArchiveController(private val archiveService: ArchiveService) {
    /**
     * 사용자 모드
     */
    // 하나 조회
    @GetMapping("/{archiveId}")
    fun getArchive(@PathVariable("archiveId") id: Long): ArchiveDetailResponse =
        archiveService.getArchive(id)

    // 목록 조회 + 검색
    @GetMapping
    fun getArchives(
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC, size = PAGE_SIZE) pageable: Pageable,
        @RequestParam(name = "category", required = false) categoryName: String?,
        @RequestParam(name = "keyword", required = false) keyword: String?
    ) = archiveService.getArchives(pageable, categoryName, keyword)


    // 공지사항 조회
    @GetMapping("/notice")
    fun getAllNotice(): ArchiveNoticeResponse = archiveService.getAllNotice()

    /**
     * 관리자 모드
     */
    // 자료글 생성
    @Auth
    @PostMapping
    fun createArchive(@Valid @ModelAttribute form: ArchiveForm) =
        archiveService.createArchive(form)

    // 자료글 수정
    @Auth
    @PutMapping("/{archiveId}")
    fun updateArchive(
        @PathVariable("archiveId") id: Long,
        @Valid @ModelAttribute form: ArchiveForm
    ):ArchiveDetailResponse = archiveService.updateArchive(id, form)

    // 자료글 삭제
    @Auth
    @DeleteMapping("/{archiveId}")
    fun deleteArchive(@PathVariable("archiveId") id: Long): BoolResponse =
        archiveService.deleteArchive(id)

    // 자료글에 첨부되어있는 파일 삭제
    @Auth
    @DeleteMapping("/{archiveId}/file/{fileId}")
    fun deleteArchiveFile(
        @PathVariable("archiveId") archiveId: Long,
        @PathVariable("fileId") fileId: Long
    ) = archiveService.deleteArchiveFile(archiveId, fileId)
}