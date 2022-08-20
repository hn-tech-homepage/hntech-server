package hntech.hntechserver.question.dto

import hntech.hntechserver.config.*
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

    @field:NotBlank @field:Size(max = MAX_TITLE_LENGTH, message = MAX_TITLE_LENGTH_MSG)
    var title: String,

    @field:NotBlank @field:Size(max = MAX_CONTENT_LENGTH, message = MAX_CONTENT_LENGTH_MSG)
    var content: String,

    @field:Pattern(regexp = REG_BOOL, message = REG_BOOL_MSG)
    var FAQ: String = "false",
)

data class UpdateClientQuestionForm(
    @field:NotBlank @field:Size(max = MAX_TITLE_LENGTH, message = MAX_TITLE_LENGTH_MSG)
    var title: String,

    @field:NotBlank @field:Size(max = MAX_CONTENT_LENGTH, message = MAX_CONTENT_LENGTH_MSG)
    var content: String,
)

data class UpdateAdminQuestionForm(
    @field:NotBlank @field:Size(max = MAX_TITLE_LENGTH, message = MAX_TITLE_LENGTH_MSG)
    var title: String,

    @field:NotBlank @field:Size(max = MAX_CONTENT_LENGTH, message = MAX_CONTENT_LENGTH_MSG)
    var content: String,

    @field:Pattern(regexp = REG_BOOL, message = REG_BOOL_MSG)
    var FAQ: String = "false"
)


data class UpdateQuestionStatusForm(
    @field:Pattern(regexp = "^(대기중|처리중|완료)$", message = "대기중, 처리중, 완료 중 하나만 입력 가능합니다.")
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
        FAQ = question.FAQ,
        title = question.title,
        content = question.content,
    )
}
