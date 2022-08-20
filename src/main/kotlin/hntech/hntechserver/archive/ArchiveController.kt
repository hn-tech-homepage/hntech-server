package hntech.hntechserver.archive

import hntech.hntechserver.utils.BoolResponse
import hntech.hntechserver.utils.auth.Auth
import hntech.hntechserver.utils.exception.ValidationException
import hntech.hntechserver.utils.logging.logger
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@RestController
@RequestMapping("/archive")
class ArchiveController(
    private val archiveService: ArchiveService,
) {
    var log = logger()

    /**
     * 사용자 모드
     */
    // 하나 조회
    @GetMapping("/{archiveId}")
    fun getArchive(@PathVariable("archiveId") id: Long) =
        ArchiveDetailResponse(archiveService.getArchive(id))

    // 목록 조회
    @GetMapping
    fun getArchives(@PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable) =
        ArchivePagedResponse(archiveService.getArchives(pageable))

    // 공지사항 조회
    @GetMapping("/notice")
    fun getAllNotice() =
        ArchiveNoticeResponse(
            archiveService.getAllNotice().map { ArchiveSimpleResponse(it) }
        )


    /**
     * 관리자 모드
     */
    // 자료글 생성
    @Auth
    @PostMapping
    fun createArchive(
        @Valid @RequestBody form: ArchiveForm,
        br: BindingResult
    ): ArchiveDetailResponse {
        if (br.hasErrors()) throw ValidationException(br)
        return ArchiveDetailResponse(archiveService.createArchive(form))
    }


    // 자료글 수정
    @Auth
    @PutMapping("/{archiveId}")
    fun updateArchive(
        @PathVariable("archiveId") id: Long,
        @Valid @RequestBody form: ArchiveForm,
        br: BindingResult
    ): ArchiveDetailResponse {
        if (br.hasErrors()) throw ValidationException(br)
        return ArchiveDetailResponse(archiveService.updateArchive(id, form))
    }


    // 자료글 삭제
    @Auth
    @DeleteMapping("/{archiveId}")
    fun deleteArchive(@PathVariable("archiveId") id: Long) =
        BoolResponse(archiveService.deleteArchive(id))

}