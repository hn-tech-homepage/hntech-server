package hntech.hntechserver.question.dto

import hntech.hntechserver.comment.Comment
import hntech.hntechserver.question.Question
import org.springframework.data.domain.Page

data class QuestionPagedResponse(
    var currentPage: Int,
    var totalPage: Int,
    var questions: List<QuestionSimpleResponse>
)

data class QuestionSimpleResponse(
    var id: Long,
    var writer: String,
    var title: String,
    var createTime: String,
    var updateTime: String
) {
    constructor(question: Question): this(
        id = question.id!!,
        writer = question.writer,
        title = question.title,
        createTime = question.createTime,
        updateTime = question.updateTime
    )
}

data class QuestionDetailResponse(
    var id: Long,
    var writer: String,
    var password: String,
    var title: String,
    var content: String,
    var createTime: String,
    var updateTime: String
) {
    constructor(question: Question): this(
        id = question.id!!,
        writer = question.writer,
        password = question.password,
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
    var title: String,
    var content: String,
    var comments: List<CommentResponse>,
    var createTime: String,
    var updateTime: String
) {
    constructor(question: Question, comments: List<CommentResponse>): this(
        id = question.id!!,
        writer = question.writer,
        password = question.password,
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