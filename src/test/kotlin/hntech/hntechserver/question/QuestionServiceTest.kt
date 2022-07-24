package hntech.hntechserver.question

import hntech.hntechserver.question.dto.QuestionCreateForm
import hntech.hntechserver.question.dto.QuestionDetailResponse
import hntech.hntechserver.utils.logger
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
internal class QuestionServiceTest {

    val log = logger()

    @Autowired
    lateinit var questionService: QuestionService

    @Autowired
    lateinit var questionRepository: QuestionRepository

    // given
    val form1 = QuestionCreateForm("userA", 1111, "test title1", "test contents1")
    val form2 = QuestionCreateForm("userB", 2222, "test title2", "test contents2")
    val form3 = QuestionCreateForm("userC", 3333, "test title3", "test contents3")

    @Test
    @DisplayName("문의사항 등록")
    fun createQuestion() {
        // when
        val expected = questionService.createQuestion(form1)
        val actual = questionRepository.findByWriter(form1.writer)

        // then
        Assertions.assertThat(actual).isEqualTo(expected)
        println(QuestionDetailResponse.toQuestionDetailResponse(expected))
    }

    @Test
    @DisplayName("문의사항 조회 페이징")
    fun findAllQuestions() {
    }

    @Test
    @DisplayName("문의사항 비밀번호로 상세 조회")
    fun findQuestionByIdAndPassword() {
    }

    @Test
    @DisplayName("문의사항 수정")
    fun updateQuestion() {
    }

    @Test
    @DisplayName("문의사항 삭제")
    fun deleteQuestion() {
    }
}