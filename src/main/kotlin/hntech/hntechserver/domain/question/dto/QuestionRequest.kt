package hntech.hntechserver.domain.question.dto

import hntech.hntechserver.common.*
import hntech.hntechserver.domain.question.model.Question
import org.springframework.web.multipart.MultipartFile
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

    var files: List<MultipartFile>? = listOf(),

    @field:Pattern(regexp = REG_BOOL, message = REG_BOOL_MSG)
    var FAQ: String = "false",
)

data class UpdateClientQuestionForm(
    @field:NotBlank @field:Size(max = MAX_TITLE_LENGTH, message = MAX_TITLE_LENGTH_MSG)
    var title: String,

    @field:NotBlank @field:Size(max = MAX_CONTENT_LENGTH, message = MAX_CONTENT_LENGTH_MSG)
    var content: String,

    var files: List<MultipartFile>? = listOf()
)

data class UpdateAdminQuestionForm(
    @field:NotBlank @field:Size(max = MAX_TITLE_LENGTH, message = MAX_TITLE_LENGTH_MSG)
    var title: String,

    @field:NotBlank @field:Size(max = MAX_CONTENT_LENGTH, message = MAX_CONTENT_LENGTH_MSG)
    var content: String,

    var files: List<MultipartFile>? = listOf(),

    @field:Pattern(regexp = REG_BOOL, message = REG_BOOL_MSG)
    var FAQ: String = "false"
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
