package hntech.hntechserver.controller

import hntech.hntechserver.file.FileService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockPart
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.multipart
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FileControllerTest {
    @Autowired
    lateinit var mvc: MockMvc

    @Test
    @DisplayName("[uploadFile] 성공")
    fun `업로드 성공`() {
        val img = MockMultipartFile("files", "test.jpg", "image/jpeg", "test".byteInputStream())
        val hwp = MockMultipartFile("files", "test.hwp", "application/msword", "test".byteInputStream())
        val pdf = MockMultipartFile("files", "test.pdf", "application/pdf", "test".byteInputStream())
        val excel = MockMultipartFile("files", "test.xlsx", "application/vnd.ms-excel", "test".byteInputStream())
        mvc.multipart("/file/upload")
        {
            file(img)
                .part(MockPart("title", "title1".toByteArray(StandardCharsets.UTF_8)))
                .part(MockPart("content", "content1".toByteArray(StandardCharsets.UTF_8)))
            file(hwp)
            file(pdf)
            file(excel)
        }
            .andDo {
                print()
            }
            .andExpect {
                status { isOk() }
            }
    }


}