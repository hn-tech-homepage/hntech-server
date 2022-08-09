package hntech.hntechserver.question

import hntech.hntechserver.logResult
import hntech.hntechserver.question.dto.*
import hntech.hntechserver.utils.logger
import org.assertj.core.api.Assertions.*
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


    @Test
    @DisplayName("문의사항 등록")
    fun createQuestion() {
        // given
        val form = QuestionCreateForm("userA", "1111", "test title1", "test contents1")

        // when
        val expected = convertDto<QuestionDetailResponse>(questionService.createQuestion(form), true)
        val actual = convertDto<QuestionDetailResponse>(questionRepository.findByWriter(form.writer)!!, true)

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
        val expected = convertDto(questionService.findAllQuestions(pageable))
        val actual = convertDto(questionRepository.findAll(pageable))

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
        assertThat(actual.id).isEqualTo(expected.id)
        logResult(actual, expected)
    }

    @Test
    @DisplayName("문의사항 내용 수정")
    fun updateQuestion() {
        // given
        val origin =
            questionService.createQuestion(QuestionCreateForm("userA", "1111", "test title1", "test contents1"))
        val updateForm = QuestionUpdateForm("test title2", "test contents2")

        // when
        val expected = questionService.updateQuestion(origin.id!!, updateForm)
        val actual = questionRepository.findById(origin.id!!).get()

        // then
        assertThat(actual).isEqualTo(expected)
        logResult(actual, expected)
    }
    
    @Test
    @DisplayName("문의사항 처리 상태 수정")
    fun updateQuestionStatus() {
        // given
        val origin =
            questionService.createQuestion(QuestionCreateForm("user", "1234", "제목", "내용"))
        val updateForm = QuestionStatusUpdateForm("처리중")

        // when
        val expected = questionService.updateQuestion(origin.id!!, updateForm)
        val actual = questionRepository.findById(origin.id!!).get()

        // then
        assertThat(actual).isEqualTo(expected)
        logResult(actual.status, expected.status)
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
            questionService.findQuestionByIdAndPassword(testQuestion.id!!, testQuestion.password)
        }.isInstanceOf(QuestionException::class.java).hasMessage(QUESTION_NOT_FOUND)
    }
}