package hntech.hntechserver.question

import hntech.hntechserver.question.dto.*
import hntech.hntechserver.utils.logger
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
internal class CommentServiceTest {
    val log = logger()
    @Autowired lateinit var questionService: QuestionService
    @Autowired lateinit var questionRepository: QuestionRepository
    @Autowired lateinit var commentService: CommentService
    @Autowired lateinit var commentRepository: CommentRepository

    // 테스트용 문의사항
    lateinit var targetQuestion: Question

    fun <T> logResult(actual: T, expected: T) {
        log.info("result\nactual \t\t: {} \nexpected \t: {}", actual.toString(), expected.toString())
    }

    @BeforeEach
    fun initQuestion() {
        questionRepository.deleteAll()
        this.targetQuestion = questionService.createQuestion(QuestionCreateForm(
            writer = "user",
            password = "1234",
            title = "title",
            content = "내용.."))
    }

    @Test
    @DisplayName("댓글 등록")
    fun createComment() {
        // given
        val form1 = CommentCreateForm("userA", "댓글 내용..")
        val form2 = CommentCreateForm("userB", "댓글 내용..")

        // when
        val expected = convertDto(commentService.createComment(targetQuestion.id!!, form1))
        commentService.createComment(targetQuestion.id!!, form2)
        val actual =
            questionService.findQuestionByIdAndPassword(targetQuestion.id!!, targetQuestion.password).comments

        // then
        assertThat(actual).contains(expected)
        assertThat(actual.get(0).writer).isEqualTo(form1.writer)
        assertThat(actual.get(1).writer).isEqualTo(form2.writer)
        logResult(actual, expected)
    }

    @Test
    @DisplayName("댓글 수정")
    fun updateComment() {
        // given
        val origin = convertDto(
            commentService.createComment(
                targetQuestion.id!!,
                CommentCreateForm("userA", "댓글 내용...")
            )
        )
        val updateForm = CommentUpdateForm("수정된 댓글 내용...")

        // when
        val expected = convertDto(commentService.updateComment(origin.id, updateForm))
        val actual = convertDto(commentRepository.findById(origin.id).get())

        // then
        assertThat(actual).isEqualTo(expected)
        assertThat(actual.content).isEqualTo(updateForm.content)
        logResult(actual, expected)
    }

    @Test
    @DisplayName("댓글 삭제")
    fun deleteComment() {
        // given
        val comment = commentService.createComment(
            targetQuestion.id!!, CommentCreateForm("user", "댓글 내용...")
        )

        // when
        commentService.deleteComment(comment.id!!)

        // then
        assertThatThrownBy {
            commentRepository.findById(comment.id!!).get()
        }.isInstanceOf(NoSuchElementException::class.java)
    }
    
    @Test
    @DisplayName("댓글 삭제(해당 문의사항이 삭제되었을 때)")
    fun deleteCommentWhenDeleteQuestion() {
        // given
        val comment = commentService.createComment(
            targetQuestion.id!!, CommentCreateForm("user", "댓글 내용...")
        )

        // when
        questionRepository.deleteById(targetQuestion.id!!)

        // then
        assertThatThrownBy {
            commentService.getComment(comment.id!!)
        }.isInstanceOf(CommentException::class.java).hasMessage(COMMENT_NOT_FOUND)
    }
}