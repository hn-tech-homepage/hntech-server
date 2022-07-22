package hntech.hntechserver.file

import hntech.hntechserver.utils.BoolResponse
import hntech.hntechserver.utils.logger
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/file")
class FileController(private val fileService: FileService) {
    val log = logger()

    @PostMapping("/upload")
    fun uploadFile(@ModelAttribute("file") files: List<MultipartFile>): FileListResponse = fileService.uploadFile(files)


}