package hntech.hntechserver.domain.comment

import hntech.hntechserver.exception.ValidationException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/question/{questionId}/comment")
class CommentController(private val commentService: CommentService) {

    @PostMapping
    fun createComment(
        @PathVariable("questionId") questionId: Long,
        @Valid @RequestBody form: CreateCommentForm,
        br: BindingResult
    ): CommentListResponse {
        if (br.hasErrors()) throw ValidationException(br)
        return CommentListResponse(
            commentService.createComment(questionId, form).map { CommentResponse(it) }
        )
    }

    @PutMapping("/{commentId}")
    fun updateComment(
        @PathVariable("questionId") questionId: Long,
        @PathVariable("commentId") commentId: Long,
        @Valid @RequestBody form: UpdateCommentForm,
        br: BindingResult
    ): CommentListResponse {
        if (br.hasErrors()) throw ValidationException(br)
        return CommentListResponse(
            commentService.updateComment(questionId, commentId, form).map { CommentResponse(it) }
        )
    }

    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @PathVariable("questionId") questionId: Long,
        @PathVariable("commentId") commentId: Long
    ) = CommentListResponse(
        commentService.deleteComment(questionId, commentId).map { CommentResponse(it) }
    )
}