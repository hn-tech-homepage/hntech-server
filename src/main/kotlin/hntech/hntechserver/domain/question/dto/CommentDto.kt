package hntech.hntechserver.domain.question.dto

import hntech.hntechserver.common.MAX_COMMENT_LENGTH
import hntech.hntechserver.common.MAX_COMMENT_LENGTH_MSG
import hntech.hntechserver.domain.question.model.Comment
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class CreateCommentForm(
    @field:NotBlank @field:Size(max = 10, message = "이름을 제대로 입력해 주세요.")
    var writer: String,

    @field:NotBlank @field:Size(max = MAX_COMMENT_LENGTH, message = MAX_COMMENT_LENGTH_MSG)
    var content: String
)

data class UpdateCommentForm(
    @field:NotBlank @field:Size(max = MAX_COMMENT_LENGTH, message = MAX_COMMENT_LENGTH_MSG)
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

data class CommentListResponse(
    var comments: List<CommentResponse>
)