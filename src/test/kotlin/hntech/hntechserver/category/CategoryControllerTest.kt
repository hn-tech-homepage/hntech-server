package hntech.hntechserver.category

import com.fasterxml.jackson.databind.ObjectMapper
import hntech.hntechserver.jsonPrint
import hntech.hntechserver.setMockSession
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoryControllerTest {

    @Autowired lateinit var mapper: ObjectMapper
    @Autowired lateinit var mvc: MockMvc
    @Autowired lateinit var categoryService: CategoryService

    @BeforeEach
    fun initDummyCategory() {
        repeat(10) {
            categoryService.createCategory(CreateCategoryForm("스프링클러$it"))
        }
        repeat(3) {
            categoryService.getCategory("스프링클러$it").update(showInMain = true)
        }
    }

    @Test
    fun `카테고리 전체 조회`() {
        val result = mvc.get("/category") {}
            .andExpect { status { isOk() } }
            .andDo { print() }
            .andReturn()
        jsonPrint(result)
    }

    @Test
    fun `메인에 보여질 카테고리 조회`() {
        val result = mvc.get("/category/main") {}
            .andExpect { status { isOk() } }
            .andReturn()
        jsonPrint(result)
    }

    @Test
    fun `카테고리 생성`() {
        val result = mvc.post("/category") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(CreateCategoryForm("신축배관"))
            session = setMockSession()
        }
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("categoryName") { value("신축배관") } }
            .andReturn()

        jsonPrint(result)
    }

    @Test
    fun `카테고리 생성 실패 - 비인가 요청`() {
        val result = mvc.post("/category") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(CreateCategoryForm("신축배관"))
        }
            .andExpect { status { isUnauthorized() } }
            .andDo { print() }
            .andReturn()

        jsonPrint(result)
    }

    @Test
    fun `카테고리 생성 실패 - 카테고리 이름 공백`() {
        val result = mvc.post("/category") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(CreateCategoryForm(" "))
            session = setMockSession()
        }
            .andExpect { status { isBadRequest() } }
            .andDo { print() }
            .andReturn()

        jsonPrint(result)
    }

    @Test
    fun `카테고리 순서 변경`() {
        val result = mvc.put("/category/sequence") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(UpdateCategorySequenceForm(2L, 7L))
            session = setMockSession()
        }
            .andExpect { status { isOk() } }
            .andDo { print() }
            .andReturn()

        jsonPrint(result)
    }

    @Test
    fun `카테고리 삭제`() {
        mvc.delete("/category/1") {
            session = setMockSession()
        }
            .andExpect { status { isOk() } }
            .andDo { print() }

    }




}