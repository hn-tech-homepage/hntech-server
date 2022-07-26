package hntech.hntechserver.question

import com.fasterxml.jackson.databind.ObjectMapper
import hntech.hntechserver.question.dto.CommentCreateForm
import hntech.hntechserver.question.dto.CommentUpdateForm
import hntech.hntechserver.question.dto.QuestionCreateForm
import hntech.hntechserver.question.dto.convertDto
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
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
internal class CommentControllerTest {
    val log = logger()
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    @Autowired lateinit var questionService: QuestionService
    @Autowired lateinit var questionRepository: QuestionRepository
    @Autowired lateinit var commentService: CommentService
    @Autowired lateinit var commentRepository: CommentRepository

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
    fun isSuccess(test: ResultActionsDsl) = test.andExpect { status { isOk() } }
    fun isFailWithValidationError(test: ResultActionsDsl) = test.andExpect { status { isBadRequest() } }
    fun isFailWithNotFoundError(test: ResultActionsDsl) = test.andExpect { status { isNotFound() } }

    // 테스트용 문의사항
    lateinit var targetQuestion: Question

    @BeforeEach
    fun initQuestion() {
        questionRepository.deleteAll()
        this.targetQuestion = questionService.createQuestion(
            QuestionCreateForm("user","1234","title","내용.."))
    }

    @Test @DisplayName("댓글 작성")
    fun createComment() {
        // given
        val uri = "/comment/" + targetQuestion.id
        val form = CommentCreateForm("userA", "userA의 댓글 내용..")

        // when
        val test = postTest(uri, form)
        val actual = convertDto(commentRepository.findAllByQuestionId(targetQuestion.id!!)[0])

        // then
        isSuccess(test, actual)
    }

    @Test @DisplayName("댓글 작성 실패(검증 오류)")
    fun createCommentFail() {
        // given
        val uri = "/comment/" + targetQuestion.id
        val form1 = CommentCreateForm("", "userA의 댓글 내용..")
        val form2 = CommentCreateForm("userA", "")
        val form3 = CommentCreateForm("", "")

        // when
        val test1 = postTest(uri, form1)
        val test2 = postTest(uri, form2)
        val test3 = postTest(uri, form3)

        // then
        isFailWithValidationError(test1)
        isFailWithValidationError(test2)
        isFailWithValidationError(test3)
    }
    
    @Test @DisplayName("댓글 작성 실패(없는 문의사항)")
    fun createCommentFail1() {
        // given
        val uri = "/comment/0"
        val form = CommentCreateForm("writer", "content...")

        // when
        val test = postTest(uri, form)

        // then
        isFailWithNotFoundError(test)
    }

    @Test @DisplayName("댓글 수정")
    fun updateComment() {
        // given
        val comment = commentService.createComment(
            targetQuestion.id!!, CommentCreateForm("user", "댓글 내용.."))
        val updateForm = CommentUpdateForm("modified content")
        val uri = "/comment/" + comment.id

        // when
        val test = putTest(uri, updateForm)
        val actual = convertDto(commentRepository.findById(comment.id!!).get())

        // then
        isSuccess(test, actual)
        assertThat(actual.content).isEqualTo(updateForm.content)
    }
    
    @Test @DisplayName("댓글 수정 실패(검증 오류)")
    fun updateCommentFail() {
        // given
        val comment = commentService.createComment(
            targetQuestion.id!!, CommentCreateForm("user", "댓글 내용.."))
        val updateForm = CommentUpdateForm("")
        val uri = "/comment/" + comment.id

        // when
        val test = putTest(uri, updateForm)

        // then
        isFailWithValidationError(test)
    }
    
    @Test @DisplayName("댓글 수정 실패(없는 댓글)")
    fun updateCommentFail1() {
        // given
        val updateForm = CommentUpdateForm("content")
        val uri = "/comment/0"

        // when
        val test = putTest(uri, updateForm)

        // then
        isFailWithNotFoundError(test)
    }

    @Test @DisplayName("댓글 삭제")
    fun deleteComment() {
        // given
        val comment = commentService.createComment(
            targetQuestion.id!!, CommentCreateForm("user", "댓글 내용.."))
        val uri = "/comment/" + comment.id

        // when
        val test = deleteTest(uri)

        // then
        isSuccess(test)
        assertThatThrownBy {
            commentRepository.findById(comment.id!!).get()
        }.isInstanceOf(NoSuchElementException::class.java)
    }

    @Test @DisplayName("댓글 삭제 실패(없는 댓글)")
    fun deleteCommentFail() {
        // given
        val uri = "/comment/0"

        // when
        val test = deleteTest(uri)

        // then
        isFailWithNotFoundError(test)
    }
}