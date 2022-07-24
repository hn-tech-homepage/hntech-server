package hntech.hntechserver.file

import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/file")
class FileController(private val fileService: FileService) {

    @PostMapping("/upload")
    fun upload(@ModelAttribute("file") files: List<MultipartFile>): FileListResponse = fileService.upload(files)


}