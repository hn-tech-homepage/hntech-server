package hntech.hntechserver.comment

import hntech.hntechserver.question.Question
import javax.validation.constraints.NotBlank

data class CreateCommentForm(
    @field:NotBlank
    var writer: String,

    @field:NotBlank
    var content: String
)

data class UpdateCommentForm(
    @field:NotBlank
    var content: String
)

data class CommentResponse(
    var id: Long,
    var writer: String,
    var sequence: Int,
    var content: String
) {
    constructor(comment: Comment): this(
        id = comment.id!!,
        writer = comment.writer,
        sequence = comment.sequence,
        content = comment.content
    )
}

fun convertEntity(comment: CreateCommentForm, question: Question): Comment {
    return Comment(
        question = question,
        writer = comment.writer,
        content = comment.content
    )
}