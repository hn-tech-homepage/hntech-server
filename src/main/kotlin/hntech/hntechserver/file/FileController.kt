package hntech.hntechserver.file

import hntech.hntechserver.archive.ArchiveRequest
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/file")
class FileController(private val fileService: FileService) {

    @PostMapping("/upload")
    fun upload(@ModelAttribute("file") files: List<MultipartFile>): FileListResponse = fileService.upload(files)



}