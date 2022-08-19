package hntech.hntechserver.file

import hntech.hntechserver.utils.BoolResponse
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.charset.Charset


@RestController
@RequestMapping("/file")
class FileController(private val fileService: FileService) {

    @PostMapping("/upload")
    fun upload(@RequestParam("file") file: MultipartFile) =
        FileResponse(fileService.saveFile(file))

    @PostMapping("/upload-all")
    fun uploadAll(@ModelAttribute files: List<MultipartFile>): FileListResponse =
        FileListResponse(
            fileService.saveAllFiles(files).map { FileResponse(it) }
        )

    @GetMapping("/download/{filename}")
    fun download(@PathVariable("filename") filename: String): ResponseEntity<Resource> {
        val resource = UrlResource("file:" + fileService.getSavedPath(filename))
        
        // 한글 깨짐 처리
        val byteArray = fileService.getOriginalFilename(filename)
            .toByteArray(Charset.forName("KSC5601"))
        val finalFilename = String(byteArray, Charset.forName("8859_1"))
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .cacheControl(CacheControl.noCache())
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${finalFilename}")
            .body(resource)
    }

    @DeleteMapping("/{fileId}")
    fun deleteById(@PathVariable("fileId") id: Long) = BoolResponse(fileService.deleteFile(id))







}