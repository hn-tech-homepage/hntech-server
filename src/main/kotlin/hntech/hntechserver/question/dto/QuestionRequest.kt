package hntech.hntechserver.question.dto

import hntech.hntechserver.question.Question
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.PositiveOrZero
import javax.validation.constraints.Size

data class CreateQuestionForm(
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

data class UpdateQuestionForm(
    @field:NotBlank
    var title: String,

    @field:NotBlank
    var content: String
)

data class UpdateQuestionFAQForm(
    @field:Pattern(regexp = "^(true|false)$", message = "true 또는 false로 입력 가능합니다.")
    var FAQ: String
)

data class UpdateQuestionStatusForm(
    var status: String
)

data class GetQuestionForm(
    @field:NotBlank
    @field:Size(min = 4, max = 4, message = "비밀번호는 4자리를 입력해주세요.")
    @field:PositiveOrZero(message = "비밀번호는 0~9의 숫자로만 입력 가능합니다.")
    var password: String
)



fun convertEntity(question : CreateQuestionForm): Question {
    return Question(
        writer = question.writer,
        password = question.password,
        FAQ = "false",
        title = question.title,
        content = question.content,
    )
}
