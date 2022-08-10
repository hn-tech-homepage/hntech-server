package hntech.hntechserver.question.dto

import hntech.hntechserver.question.Comment
import hntech.hntechserver.question.Question
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.PositiveOrZero
import javax.validation.constraints.Size

data class QuestionCreateForm(
    @field:NotBlank
    var writer: String,
    
    @field:NotBlank
    @field:Size(min = 4, max = 4, message = "비밀번호는 4자리를 입력해주세요.")
    @field:PositiveOrZero(message = "비밀번호는 0~9의 숫자로만 입력 가능합니다.")
    var password: String,

    @field:NotBlank
    var title: String,

    @field:NotBlank
    var content: String
)

data class QuestionUpdateForm(
    @field:NotBlank
    var title: String,

    @field:NotBlank
    var content: String
)

data class QuestionFAQUpdateForm(
    @field:Pattern(regexp = "^(true|false)$", message = "true 또는 false로 입력 가능합니다.")
    var FAQ: String
)

data class QuestionFindForm(
    @field:NotBlank
    @field:Size(min = 4, max = 4, message = "비밀번호는 4자리를 입력해주세요.")
    @field:PositiveOrZero(message = "비밀번호는 0~9의 숫자로만 입력 가능합니다.")
    var password: String
)

data class CommentCreateForm(
    @field:NotBlank
    var writer: String,

    @field:NotBlank
    var content: String
)

data class CommentUpdateForm(
    @field:NotBlank
    var content: String
)

fun convertEntity(question : QuestionCreateForm): Question {
    return Question(
        writer = question.writer,
        password = question.password,
        FAQ = false,
        title = question.title,
        content = question.content,
    )
}
fun convertEntity(comment: CommentCreateForm, question: Question): Comment {
    return Comment(
        question = question,
        writer = comment.writer,
        content = comment.content
    )
}