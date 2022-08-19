package hntech.hntechserver.question.dto

import hntech.hntechserver.comment.CommentResponse
import hntech.hntechserver.question.Question
import hntech.hntechserver.utils.function.isNewCheck
import org.springframework.data.domain.Page

data class QuestionPagedResponse(
    var currentPage: Int,
    var totalPages: Int,
    var totalElements: Long,
    var questions: List<QuestionSimpleResponse>
) {
    constructor(questions: Page<Question>) : this(
        currentPage = questions.number,
        totalPages = questions.totalPages,
        totalElements = questions.totalElements,
        questions = questions.map { QuestionSimpleResponse(it) }.toList()
    )
}

data class QuestionSimpleResponse(
    var id: Long,
    var writer: String,
    var title: String,
    var new: String = "false",
    var status: String = "",
    var createTime: String,
) {
    constructor(question: Question): this(
        id = question.id!!,
        writer = question.writer,
        title = question.title,
        new = isNewCheck(question.createTime),
        status = question.status,
        createTime = question.createTime,
    )
}

data class QuestionDetailResponse(
    var id: Long,
    var writer: String,
    var password: String,
    var title: String,
    var content: String,
    var comments: List<CommentResponse>,
    var createTime: String,
    var updateTime: String
) {
    constructor(question: Question): this(
        id = question.id!!,
        writer = question.writer,
        password = question.password,
        title = question.title,
        content = question.content,
        comments = question.comments.map { CommentResponse(it) },
        createTime = question.createTime,
        updateTime = question.updateTime
    )
}