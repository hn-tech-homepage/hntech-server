package hntech.hntechserver.archive

import hntech.hntechserver.utils.logger
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/archive")
class ArchiveController(private val archiveService: ArchiveService) {
    var log = logger()

    /**
     * 자료 생성
     */
    @PostMapping("")
    fun createArchive(@RequestBody form: ArchiveRequest): ArchiveSimpleResponse = archiveService.createArchive(form)


    /**
     * 자료 조회
     */

    /**
     * 자료 수정
     */

    /**
     * 자료 삭제
     */
}