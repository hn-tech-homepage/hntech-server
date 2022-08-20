package hntech.hntechserver.question

import hntech.hntechserver.logResult
import hntech.hntechserver.question.dto.CreateQuestionForm
import hntech.hntechserver.question.dto.UpdateQuestionFAQForm
import hntech.hntechserver.question.dto.UpdateClientQuestionForm
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class QuestionServiceTest {

    @Autowired lateinit var questionService: QuestionService
    @Autowired lateinit var questionRepository: QuestionRepository

    val dummyForm = CreateQuestionForm(
        "userA",
        "1111",
        "false",
        "test title1",
        "test contents1"
    )

    private fun setDummyQuestion() =
        questionService.createQuestion(dummyForm)

    @Test
    fun `문의사항 등록 성공`() {
        val expected: Question = questionService.createQuestion(dummyForm)
        val actual: Question = questionRepository.findByWriter(dummyForm.writer)!!

        expected shouldBe actual
    }

    @Test
    fun `문의사항 목록 조회 (페이징)`() {
        // given
        val pageable: Pageable = PageRequest.of(0, 15, Sort.Direction.DESC, "id")
        repeat(30) { setDummyQuestion() }

        // when
        val expected: Page<Question> = questionService.findAllQuestions(pageable)
        val actual: Page<Question> = questionRepository.findAll(pageable)

        // then
        assertSoftly(expected) {
            totalPages shouldBe 2
            totalElements shouldBe 30
            it shouldBe actual
        }
    }

    @Test
    fun `자주 묻는 질문 조회 성공`() {
        // given
        val idList = mutableListOf<Long>()
        val pageable: Pageable = PageRequest.of(0, 15, Sort.Direction.DESC, "id")
        repeat(20) {
            idList.add(setDummyQuestion().id!!)
        }
        repeat(10) {
            questionService.updateFAQ(idList[it], UpdateQuestionFAQForm("true"))
        }

        // when
        val faqList = questionService.findFAQ(pageable)

        // then
        faqList.forEach { it.FAQ shouldBe "true" }
    }

    @Test
    fun `문의사항 비밀번호로 상세 조회 성공`() {
        // given
        val actual = setDummyQuestion()

        // when
        val expected =
            questionService.findQuestionByIdAndPassword(actual.id!!, actual.password)

        // then
        expected.id shouldBe actual.id
    }

    @Test
    fun `문의사항 내용 수정 성공`() {
        // given
        val origin = setDummyQuestion()
        val updateForm = UpdateClientQuestionForm("test title2", "test contents2")

        // when
        val expected = questionService.updateQuestion(origin.id!!, updateForm)
        val actual = questionRepository.findById(origin.id!!).get()

        // then
        expected shouldBe actual
        logResult(actual, expected)
    }
    
    @Test
    fun `자주 묻는 질문으로 수정 성공`() {
        // given
        val origin = setDummyQuestion()
        val updateForm = UpdateQuestionFAQForm("처리중")

        // when
        val expected = questionService.setStatusComplete(origin.id!!, updateForm.FAQ)
        val actual = questionRepository.findById(origin.id!!).get()

        // then
        assertSoftly(expected) {
            FAQ shouldBe actual.FAQ
            it shouldBe actual
        }
    }

    @Test
    fun `문의사항 삭제 성공`() {
        // given
        val testQuestion = setDummyQuestion()

        // when
        questionService.deleteQuestion(testQuestion.id!!)

        // then
        shouldThrow<QuestionException> {
            questionService.findQuestionByIdAndPassword(testQuestion.id!!, testQuestion.password)
        }.message shouldBe QUESTION_NOT_FOUND
    }
}