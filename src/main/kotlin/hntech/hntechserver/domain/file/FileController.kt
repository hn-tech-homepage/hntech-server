package hntech.hntechserver.domain.file

import hntech.hntechserver.utils.BoolResponse
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
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

    @ApiOperation(
        value = "파일 업로드",
        notes = "저장 위치 설정하여 파일을 서버에 업로드"
    )
    @ApiImplicitParams(
        ApiImplicitParam(
            name = "type",
            value = "{type} <= {admin, category, product, archive} \n " +
                    "업로드 하려는 파일의 타입을 지정하기 위함 \n " +
                    "해당 파일의 도메인을 type 으로 요청",
            required = true
        )
    )
    @PostMapping("/{type}/upload")
    fun upload(@PathVariable("type") type: String, @RequestParam("file") file: MultipartFile) =
        FileResponse(fileService.saveFile(file, type))

    @PostMapping("/{type}/upload-all")
    fun uploadAll(
        @PathVariable("type") type: String,
        @ModelAttribute files: List<MultipartFile>
    ) = FileListResponse(fileService.saveAllFiles(type, files).map { FileResponse(it) })

    @GetMapping("/download/{filename}")
    fun download(@PathVariable("filename") filename: String): ResponseEntity<Resource> {
        val resource = UrlResource("file:" + fileService.getFile(filename).savedPath)
        
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
    fun deleteById(@PathVariable("fileId") id: Long): BoolResponse {
        fileService.deleteFile(id)
        return BoolResponse()
    }
}