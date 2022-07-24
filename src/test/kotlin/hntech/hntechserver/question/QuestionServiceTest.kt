package hntech.hntechserver.question

import hntech.hntechserver.question.dto.QuestionCreateForm
import hntech.hntechserver.question.dto.QuestionDetailResponse
import hntech.hntechserver.question.dto.QuestionPagedResponse
import hntech.hntechserver.question.dto.QuestionUpdateForm
import hntech.hntechserver.utils.logger
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
internal class QuestionServiceTest {

    val log = logger()

    @Autowired
    lateinit var questionService: QuestionService

    @Autowired
    lateinit var questionRepository: QuestionRepository

    fun <T> logResult(actual: T, expected: T) {
        log.info("result\nactual \t\t: {} \nexpected \t: {}", actual.toString(), expected.toString())
    }

    @Test
    @DisplayName("문의사항 등록")
    fun createQuestion() {
        // given
        val form = QuestionCreateForm("userA", "1111", "test title1", "test contents1")

        // when
        val expected = QuestionDetailResponse.toQuestionDetailResponse(questionService.createQuestion(form))
        val actual = QuestionDetailResponse.toQuestionDetailResponse(questionRepository.findByWriter(form.writer)!!)

        // then
        assertThat(actual).isEqualTo(expected)
        logResult(actual, expected)
    }

    @Test
    @DisplayName("문의사항 조회 페이징")
    fun findAllQuestions() {
        // given
        var page = 0; var size = 10
        var pageable: Pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id")
        for (i in 1..30) {
            questionService.createQuestion(QuestionCreateForm("user" + i, i.toString(), "title" + i, "content" + i))
        }

        // when
        val expected = QuestionPagedResponse.toQuestionPagedResponse(questionService.findAllQuestions(pageable))
        val actual = QuestionPagedResponse.toQuestionPagedResponse(questionRepository.findAll(pageable))

        // then
        assertThat(actual).isEqualTo(expected)
        logResult(actual, expected)
    }

    @Test
    @DisplayName("문의사항 비밀번호로 상세 조회")
    fun findQuestionByIdAndPassword() {
        // given
        val actual =
            questionService.createQuestion(QuestionCreateForm("userA", "1111", "test title1", "test contents1"))

        // when
        val expected =
            questionService.findQuestionByIdAndPassword(actual.id!!, actual.password)

        // then
        assertThat(actual).isEqualTo(expected)
        logResult(actual, expected)
    }

    @Test
    @DisplayName("문의사항 수정")
    fun updateQuestion() {
        // given
        val origin =
            questionService.createQuestion(QuestionCreateForm("userA", "1111", "test title1", "test contents1"))
        val updateForm = QuestionUpdateForm("userB", "test title2", "test contents2")

        // when
        val expected = questionService.updateQuestion(origin.id!!, updateForm)
        val actual = questionRepository.findById(origin.id!!).get()

        // then
        assertThat(actual).isEqualTo(expected)
        logResult(actual, expected)
    }

    @Test
    @DisplayName("문의사항 삭제")
    fun deleteQuestion() {
        // given
        val testQuestion =
            questionService.createQuestion(QuestionCreateForm("userA", "1111", "test title1", "test contents1"))

        // when
        questionService.deleteQuestion(testQuestion.id!!)

        // then
        assertThatThrownBy {
            questionRepository.findById(testQuestion.id!!).orElseThrow { throw NoSuchElementException() }
        }.isInstanceOf(NoSuchElementException::class.java)
    }
}