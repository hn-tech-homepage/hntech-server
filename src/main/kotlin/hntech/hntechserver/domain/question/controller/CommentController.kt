package hntech.hntechserver.domain.question.controller

import hntech.hntechserver.domain.question.QuestionService
import hntech.hntechserver.domain.question.dto.CommentListResponse
import hntech.hntechserver.domain.question.dto.CreateCommentForm
import hntech.hntechserver.domain.question.dto.UpdateCommentForm
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/question/{questionId}/comment")
//@RequestMapping("/question/{questionId}/comment")
class CommentController(private val questionService: QuestionService) {

    @PostMapping
    fun createComment(
        @PathVariable("questionId") questionId: Long,
        @Valid @RequestBody form: CreateCommentForm
    ): CommentListResponse =
        questionService.createComment(questionId, form)

    @PutMapping("/{commentId}")
    fun updateComment(
        @PathVariable("questionId") questionId: Long,
        @PathVariable("commentId") commentId: Long,
        @Valid @RequestBody form: UpdateCommentForm
    ): CommentListResponse =
        questionService.updateComment(questionId, commentId, form)

    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @PathVariable("questionId") questionId: Long,
        @PathVariable("commentId") commentId: Long
    ): CommentListResponse =
        questionService.deleteComment(questionId, commentId)

}