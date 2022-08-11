package hntech.hntechserver.archive

import hntech.hntechserver.utils.auth.Auth
import hntech.hntechserver.utils.BoolResponse
import hntech.hntechserver.utils.config.PAGE_SIZE
import hntech.hntechserver.utils.logger
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

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
    @GetMapping("/all")
    fun getArchives(@PageableDefault(size = PAGE_SIZE, sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable) =
        ArchiveListResponse(archiveService.getArchives(pageable))

    /**
     * 관리자 모드
     */
    // 자료글 생성
    @Auth
    @PostMapping
    fun createArchive(@RequestBody form: ArchiveForm) =
        ArchiveSimpleResponse(archiveService.createArchive(form))

    // 자료글 수정
    @Auth
    @PutMapping("/{archiveId}")
    fun updateArchive(@PathVariable("archiveId") id: Long, @RequestBody form: ArchiveForm) =
        ArchiveSimpleResponse(archiveService.updateArchive(id, form))

    // 자료글 삭제
    @Auth
    @DeleteMapping("/{archiveId}")
    fun deleteArchive(@PathVariable("archiveId") id: Long) =
        BoolResponse(archiveService.deleteArchive(id))

}