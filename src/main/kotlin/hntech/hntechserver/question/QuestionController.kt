package hntech.hntechserver.question

import hntech.hntechserver.domain.Comment
import hntech.hntechserver.question.dto.*
import hntech.hntechserver.utils.logger
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/question")
class QuestionController(private val questionService: QuestionService) {
    val log = logger()

    @PostMapping("/question")
    fun createQuestion(question: QuestionCreateForm): QuestionDetailResponse {
        return QuestionDetailResponse.toQuestionDetailResponse(questionService.createQuestion(question))
    }

    @GetMapping
    fun getAllQuestions(
        @PageableDefault(size = 10, sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): QuestionPagedResponse {
        return QuestionPagedResponse.toQuestionPagedResponse(questionService.findAllQuestions(pageable))
    }

    // 비밀번호로 문의사항 상세 조회
    @PostMapping("/question/{question_id}")
    fun getQuestion(@PathVariable("question_id") id: Long, password: String): QuestionDetailResponse {
        return QuestionDetailResponse.toQuestionDetailResponse(questionService.findQuestionByIdAndPassword(id, password))
    }

    @PutMapping("/question/{question_id}")
    fun updateQuestion(@PathVariable("question_id") id: Long, form: QuestionUpdateForm): QuestionDetailResponse {
        return QuestionDetailResponse.toQuestionDetailResponse(questionService.updateQuestion(id, form))
    }

    @DeleteMapping("/question/{question_id}")
    fun deleteQuestion(@PathVariable("question_id") id: Long) = questionService.deleteQuestion(id)
}

@RestController
@RequestMapping("/api/comment")
class CommentController(private val commentService: CommentService) {

    @PostMapping("/{question_id}")
    fun createComment(@PathVariable("question_id") questionId: Long, form: CommentCreateForm): CommentResponse {
        return CommentResponse.toCommentResponse(commentService.createComment(questionId, form))
    }

    @PutMapping("/{question_id}/{comment_id}")
    fun updateComment(
        @PathVariable("question_id") questionId: Long,
        @PathVariable("comment_id") commentId: Long,
        form: CommentUpdateForm
    ) {
        commentService.updateComment(questionId, commentId, form)
    }

    @DeleteMapping("/{question_id}/{comment_id}")
    fun deleteComment(
        @PathVariable("question_id") questionId: Long,
        @PathVariable("comment_id") commentId: Long
    ) {
        commentService.deleteComment(questionId, commentId)
    }
}