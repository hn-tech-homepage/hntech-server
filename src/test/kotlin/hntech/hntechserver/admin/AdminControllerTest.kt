package hntech.hntechserver.admin

import com.fasterxml.jackson.databind.ObjectMapper
import hntech.hntechserver.domain.admin.*
import hntech.hntechserver.domain.file.FileRepository
import hntech.hntechserver.domain.file.FileService
import hntech.hntechserver.setMockSession
import hntech.hntechserver.testFile
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.multipart
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminControllerTest {

    @Autowired lateinit var mvc: MockMvc
    @Autowired lateinit var mapper: ObjectMapper
    @Autowired lateinit var fileRepository: FileRepository
    @Autowired lateinit var fileService: FileService
    @Autowired lateinit var adminService: AdminService

    @BeforeEach
    fun `관리자 생성 및 세션 생성`() {
        adminService.createAdmin("1234")
    }

    @AfterEach
    fun `mock 파일 삭제`() = fileService.deleteAllFiles(fileRepository.findAll())

    @Test
    fun `관리자 로그인 성공`() {
        mvc.post("/admin/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(mapOf("password" to "1234"))
        }
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("result") { value(true) } }
            .andDo { print() }
    }

    @Test
    fun `관리자 로그인 실패 - 비밀번호 틀림`() {
        mvc.post("/admin/login") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(mapOf("password" to "1111"))
        }
            .andExpect { status { isUnauthorized() } }
            .andDo { print() }
    }

    @Test
    fun `관리자 로그아웃 성공`() {
        mvc.post("/admin/logout") {
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect { status { isOk() } }
            .andDo { print() }
    }

    @Test
    fun `관리자 비밀번호 변경 성공`() {
        val form = UpdatePasswordForm("1234", "1234", "1111")
        mvc.post("/admin/password") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(form)
            session = setMockSession()
        }
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("newPassword") { value("1111") } }
            .andDo { print() }
    }

    @Test
    fun `인사말 조회`() {
        mvc.get("/admin/introduce") {}
            .andExpect { status { isOk() }}
            .andExpect { jsonPath("newIntroduce") { value("") } }
            .andDo { print() }
    }

    @Test
    fun `인사말 수정 성공`() {
        mvc.post("/admin/introduce") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(IntroduceDto("안녕"))
            session = setMockSession()
        }
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("newIntroduce") { value("안녕") } }
            .andDo { print() }
    }

    @Test
    fun `조직도 수정 성공`() {
        mvc.multipart("/admin/image") {
            file(testFile)
            param("where", ORG_CHART)
            session = setMockSession()
        }
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("where") { value(ORG_CHART) } }
            .andDo { print() }
    }

    @Test
    fun `CI 수정 성공`() {
        mvc.multipart("/admin/image") {
            file(testFile)
            param("where", CI)
            session = setMockSession()
        }
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("where") { value(CI) } }
            .andDo { print() }
    }

    @Test
    fun `연혁 수정 성공`() {
        mvc.multipart("/admin/image") {
            file(testFile)
            param("where", HISTORY)
            session = setMockSession()
        }
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("where") { value(HISTORY) } }
            .andDo { print() }
    }

    @Test
    fun `footer 조회`() {
        mvc.get("/admin/footer") {}
            .andExpect { status { isOk() } }
            .andDo { print() }
    }

    @Test
    fun `footer 수정 성공`() {
        val form = FooterDto("처인구", "A/S", "전화번호", "FAX")
        mvc.post("/admin/footer") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(form)
            session = setMockSession()
        }
            .andExpect { status { isOk() } }
            .andDo { print() }
    }


}