package hntech.hntechserver.question

import com.fasterxml.jackson.databind.ObjectMapper
import hntech.hntechserver.question.dto.*
import hntech.hntechserver.utils.logger
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
internal class QuestionControllerTest {
    val log = logger()

    @Autowired lateinit var mockMvc: MockMvc

    @Autowired lateinit var mapper: ObjectMapper

    @Autowired lateinit var questionService: QuestionService

    @Autowired lateinit var questionRepository: QuestionRepository
    
    @BeforeEach
    fun clearRepository() = questionRepository.deleteAll()

    fun getTest(uri: String): ResultActionsDsl {
        return mockMvc.get(uri) {
            accept = MediaType.APPLICATION_JSON
        }
    }
    fun <T> postTest(uri: String, data: T): ResultActionsDsl {
        return mockMvc.post(uri) {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(data)
        }
    }
    fun <T> putTest(uri: String, data: T): ResultActionsDsl {
        return mockMvc.put(uri) {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(data)
        }
    }
    fun <T> patchTest(uri: String, data: T): ResultActionsDsl {
        return mockMvc.patch(uri) {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(data)
        }
    }
    fun deleteTest(uri: String): ResultActionsDsl {
        return mockMvc.delete(uri) {
            contentType = MediaType.APPLICATION_JSON
        }
    }
    fun <T> isSuccess(test: ResultActionsDsl, actual: T) {
        test.andExpect {
            status { isOk() }
            content { mapper.writeValueAsString(actual) }
        }
    }
    fun isSuccess(test: ResultActionsDsl) {
        test.andExpect { status { isOk() } }
    }
    fun isFailWithValidationError(test: ResultActionsDsl) {
        test.andExpect { status { isBadRequest() } }
    }
    fun isFailWithNotFoundError(test: ResultActionsDsl) {
        test.andExpect { status { isNotFound() } }
    }

    @Test
    @DisplayName("문의사항 등록")
    fun createQuestion() {
        // given
        val uri = "/question"
        val form = QuestionCreateForm(
            writer = "user",
            password = "1234",
            title = "문의사항 제목",
            content = "문의사항 내용.."
        )

        // when
        val test = postTest(uri, form)
        val actual = questionRepository.findAll()[0]

        // then
        isSuccess(test, actual)
    }
    
    @Test
    @DisplayName("문의사항 등록 실패(검증 오류)")
    fun createQuestionFail() {
        // given
        val uri = "/question"
        val form1 = QuestionCreateForm("", "1234", "title", "content")
        val form2 = QuestionCreateForm("writer", "", "title", "content")
        val form3 = QuestionCreateForm("writer", "1234", "", "content")
        val form4 = QuestionCreateForm("writer", "1234", "title", "")
        val form5 = QuestionCreateForm("", "", "", "")

        // when
        val test1 = postTest(uri, form1)
        val test2 = postTest(uri, form2)
        val test3 = postTest(uri, form3)
        val test4 = postTest(uri, form4)
        val test5 = postTest(uri, form5)

        // then
        isFailWithValidationError(test1)
        isFailWithValidationError(test2)
        isFailWithValidationError(test3)
        isFailWithValidationError(test4)
        isFailWithValidationError(test5)
    }

    @Test
    @DisplayName("문의사항 조회 페이징")
    fun getAllQuestions() {
        // given
        val uri = "/question"
        var page = 0; var size = 10
        var pageable: Pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id")
        for (i in 1..30) {
            questionRepository.save(convertEntity(
                QuestionCreateForm(
                writer = "user$i",
                password = "1234",
                title = "title$i",
                content = "문의 내용..."
            )))
        }

        // when
        val test = getTest(uri)
        val actual = convertDto(questionService.findAllQuestions(pageable))

        // then
        isSuccess(test, actual)
    }

    @Test
    @DisplayName("문의사항 상세 조회")
    fun getQuestion() {
        // given
        val question = questionService.createQuestion(
            QuestionCreateForm("user", "1234", "title", "content"))
        val form = QuestionFindForm("1234")
        val uri = "/question/" + question.id

        // when
        val test = postTest(uri, form)
        val actual = questionService.findQuestionByIdAndPassword(question.id!!, form.password)

        // then
        isSuccess(test, actual)
    }
    
    @Test
    @DisplayName("문의사항 상세 조회 실패(검증 오류)")
    fun getQuestionFail() {
        // given
        val question = questionService.createQuestion(
            QuestionCreateForm("user", "1234", "title", "content"))
        val form = QuestionFindForm("")
        val uri = "/question/" + question.id

        // when
        val test = postTest(uri, form)

        // then
        isFailWithValidationError(test)
    }

    @Test
    @DisplayName("문의사항 상세 조회 실패(없는 문의사항)")
    fun getQuestionFail1() {
        // given
        val form = QuestionFindForm("1234")
        val uri = "/question/0"

        // when
        val test = postTest(uri, form)

        // then
        isFailWithNotFoundError(test)
    }

    @Test
    @DisplayName("문의사항 상세 조회 실패(비밀번호 불일치)")
    fun getQuestionFail2() {
        // given
        val question = questionService.createQuestion(
            QuestionCreateForm("user", "1234", "title", "content"))
        val form = QuestionFindForm("0000")
        val uri = "/question/" + question.id

        // when
        val test = postTest(uri, form)

        // then
        isFailWithNotFoundError(test)
    }

    @Test
    @DisplayName("문의사항 내용 수정")
    fun updateQuestionForm() {
        // given
        val origin = questionService.createQuestion(
            QuestionCreateForm("user", "1234", "title", "content"))
        val updateForm = QuestionUpdateForm("modified title", "modified content")
        val uri = "/question/" + origin.id
        
        // when
        val test = putTest(uri, updateForm)
        val actual = convertDto<QuestionDetailResponse>(questionRepository.findById(origin.id!!).get(), true)

        // then
        isSuccess(test, actual)
        assertThat(actual.title).isEqualTo(updateForm.title)
        assertThat(actual.content).isEqualTo(updateForm.content)
    }
    
    @Test
    @DisplayName("문의사항 내용 수정 실패(검증 오류)")
    fun updateQuestionFormFail() {
        // given
        val origin = questionService.createQuestion(
            QuestionCreateForm("user", "1234", "title", "content"))
        val updateForm = QuestionUpdateForm("", "")
        val uri = "/question/" + origin.id

        // when
        val test = putTest(uri, updateForm)

        // then
        isFailWithValidationError(test)
    }

    @Test
    @DisplayName("문의사항 내용 수정 실패(없는 문의사항)")
    fun updateQuestionFormFail1() {
        // given
        val updateForm = QuestionUpdateForm("modified title", "modified content")
        val uri = "/question/0"

        // when
        val test = putTest(uri, updateForm)

        // then
        isFailWithNotFoundError(test)
    }
    
    @Test
    @DisplayName("문의사항 처리 상태 수정")
    fun updateQuestionStatus() {
        // given
        val origin = questionService.createQuestion(
            QuestionCreateForm("user", "1234", "title", "content"))
        val updateForm = QuestionStatusUpdateForm("진행중")
        val uri = "/question/" + origin.id

        // when
        val test = patchTest(uri, updateForm)

        // then
        isSuccess(test)
        assertThat(questionRepository.findById(origin.id!!).get().status).isEqualTo(updateForm.status)
    }
    
    @Test
    @DisplayName("문의사항 처리 상태 수정 실패(없는 문의사항)")
    fun updateQuestionStatusFail() {
        // given
        val uri = "/question/0"
        val updateForm = QuestionStatusUpdateForm("진행중")

        // when
        val test = patchTest(uri, updateForm)

        // then
        isFailWithNotFoundError(test)
    }

    @Test
    @DisplayName("문의사항 처리 상태 수정 실패(검증 오류)")
    fun updateQuestionStatusFail1() {
        // given
        val origin = questionService.createQuestion(
            QuestionCreateForm("user", "1234", "title", "content"))
        val updateForm1 = QuestionStatusUpdateForm("완료되었음")
        val updateForm2 = QuestionStatusUpdateForm("")
        val uri = "/question/" + origin.id

        // when
        val test1 = patchTest(uri, updateForm1)
        val test2 = patchTest(uri, updateForm2)

        // then
        isFailWithValidationError(test1)
        isFailWithValidationError(test2)
    }

    @Test
    @DisplayName("문의사항 삭제")
    fun deleteQuestion() {
        // given
        val origin = questionService.createQuestion(
            QuestionCreateForm("user", "1234", "title", "content"))
        val uri = "/question/" + origin.id

        // when
        val test = deleteTest(uri)

        // then
        isSuccess(test)
        assertThatThrownBy {
            questionRepository.findById(origin.id!!).get()
        }.isInstanceOf(NoSuchElementException::class.java)
    }

    @Test
    @DisplayName("문의사항 삭제 실패(없는 문의사항)")
    fun deleteQuestionFail() {
        // given
        val uri = "/question/0"

        // when
        val test = deleteTest(uri)

        // then
        isFailWithNotFoundError(test)
    }
}