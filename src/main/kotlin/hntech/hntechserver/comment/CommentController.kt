package hntech.hntechserver.comment

import hntech.hntechserver.utils.exception.ValidationException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/comment")
class CommentController(private val commentService: CommentService) {

    @PostMapping("/{questionId}")
    fun createComment(@PathVariable("questionId") questionId: Long,
                      @Valid @RequestBody form: CreateCommentForm,
                      bindingResult: BindingResult
    ): CommentResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return CommentResponse(commentService.createComment(questionId, form))
    }

    @PutMapping("/{commentId}")
    fun updateComment(@PathVariable("commentId") commentId: Long,
                      @Valid @RequestBody form: UpdateCommentForm,
                      bindingResult: BindingResult
    ): CommentResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return CommentResponse(commentService.updateComment(commentId, form))
    }

    @DeleteMapping("/{commentId}")
    fun deleteComment(@PathVariable("commentId") commentId: Long) =
        commentService.deleteComment(commentId)
}