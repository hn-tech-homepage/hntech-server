package hntech.hntechserver.question.dto

import hntech.hntechserver.question.Comment
import hntech.hntechserver.question.Question
import org.springframework.data.domain.Page
import java.time.LocalDateTime

data class QuestionPagedResponse(
    var currentPage: Int,
    var totalPage: Int,
    var questions: List<QuestionSimpleResponse>
)

data class QuestionSimpleResponse(
    var id: Long,
    var writer: String,
    var title: String,
    var isFAQ: String,
    var createTime: LocalDateTime,
    var updateTime: LocalDateTime
) {
    constructor(question: Question): this(
        id = question.id!!,
        writer = question.writer,
        title = question.title,
        isFAQ = question.isFAQ,
        createTime = question.createTime,
        updateTime = question.updateTime
    )
}

data class QuestionDetailResponse(
    var id: Long,
    var writer: String,
    var password: String,
    var isFAQ: String,
    var title: String,
    var content: String,
    var createTime: LocalDateTime,
    var updateTime: LocalDateTime
) {
    constructor(question: Question): this(
        id = question.id!!,
        writer = question.writer,
        password = question.password,
        isFAQ = question.isFAQ,
        title = question.title,
        content = question.content,
        createTime = question.createTime,
        updateTime = question.updateTime
    )
}

data class QuestionCompleteResponse(
    var id: Long,
    var writer: String,
    var password: String,
    var isFAQ: String,
    var title: String,
    var content: String,
    var comments: List<CommentResponse>,
    var createTime: LocalDateTime,
    var updateTime: LocalDateTime
) {
    constructor(question: Question, comments: List<CommentResponse>): this(
        id = question.id!!,
        writer = question.writer,
        password = question.password,
        isFAQ = question.isFAQ,
        title = question.title,
        content = question.content,
        comments = comments,
        createTime = question.createTime,
        updateTime = question.updateTime
    )
}

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

fun convertDto(questions: Page<Question>): QuestionPagedResponse {
    var questionList: ArrayList<QuestionSimpleResponse> = arrayListOf()
    questions.forEach { questionList.add(QuestionSimpleResponse(it)) }
    return QuestionPagedResponse(
        currentPage = questions.number,
        totalPage = questions.totalPages,
        questions = questionList
    )
}