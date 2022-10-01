package hntech.hntechserver.domain.question.dto

import hntech.hntechserver.domain.file.FileResponse
import hntech.hntechserver.domain.question.model.Question
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
    var title: String,
    var content: String,
    var comments: List<CommentResponse>,
    var files: List<FileResponse>,
    var createTime: String,
    var status: String,
) {
    constructor(question: Question): this(
        id = question.id!!,
        writer = question.writer,
        title = question.title,
        content = question.content,
        comments = question.comments.map { CommentResponse(it) },
        files = question.files.map { FileResponse(it) },
        createTime = question.createTime,
        status = question.status,
    )
}