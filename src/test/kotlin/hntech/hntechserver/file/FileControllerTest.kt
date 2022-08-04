
package hntech.hntechserver.file

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockPart
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.multipart
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.function.RequestPredicates.contentType
import java.nio.charset.StandardCharsets

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FileControllerTest {
    @Autowired lateinit var mvc: MockMvc
    @Autowired lateinit var fileService: FileService
    @Autowired lateinit var fileRepository: FileRepository

    @AfterEach
    fun `mock 파일 삭제`() = fileService.deleteAllFiles(fileRepository.findAll())

    @Test
    fun `단일 업로드 성공`() {
        val img = MockMultipartFile("file", "test.jpg", "image/jpeg", "test".byteInputStream())
        mvc.multipart("/file/upload") { file(img) }
            .andDo { print() }
            .andExpect { status { isOk() } }
    }

    @Test
    fun `다중 업로드 성공`() {
        val img = MockMultipartFile("files", "test.jpg", "image/jpeg", "test".byteInputStream())
        val hwp = MockMultipartFile("files", "test.hwp", "application/msword", "test".byteInputStream())
        val pdf = MockMultipartFile("files", "test.pdf", "application/pdf", "test".byteInputStream())
        val excel = MockMultipartFile("files", "test.xlsx", "application/vnd.ms-excel", "test".byteInputStream())
        mvc.multipart("/file/upload-all") {
            file(img)
            file(hwp)
            file(pdf)
            file(excel)
        }
            .andDo { print() }
            .andExpect { status { isOk() } }
    }

    @Test
    fun `파일 다운로드 성공`() {
        // given
        val img = MockMultipartFile("files", "test.jpg", "image/jpeg", "test".byteInputStream())
        val savedFile: File = fileService.saveFile(img)

        mvc.get("/file/download/${savedFile.serverFilename}") {}
            .andDo { print() }
            .andExpect { status { isOk() } }
    }



}