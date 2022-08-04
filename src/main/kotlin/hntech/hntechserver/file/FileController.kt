package hntech.hntechserver.file

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
    fun upload(@RequestParam("file") file: MultipartFile): FileResponse =
        convertDto(fileService.saveFile(file))

    @PostMapping("/upload-all")
    fun uploadAll(@ModelAttribute files: List<MultipartFile>): FileListResponse =
        convertDto(fileService.saveAllFiles(files))

    @GetMapping("/download/{filename}")
    fun download(@PathVariable("filename") filename: String): ResponseEntity<Resource> {
        val resource = UrlResource("file:" + fileService.getSavedPath(filename))
        
        // 한글 깨짐 처리
        val byteArray = fileService.getOriginalFilename(filename)
            .toByteArray(Charset.forName("KSC5601"))
        val finalFilename = String(byteArray, Charset.forName("8859_1"))
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .cacheControl(CacheControl.noCache()) // 정적 리소스를 내려줄때 성능을 높히기 위해 브라우저에서 캐싱하는데, 이러면 오래된 리소스를 내려줄 위험이 있어서 캐시를 끈다.
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=${finalFilename}")
            .body(resource)
    }







}