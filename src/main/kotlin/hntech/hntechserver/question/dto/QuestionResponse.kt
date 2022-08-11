package hntech.hntechserver.question.dto

import hntech.hntechserver.comment.CommentResponse
import hntech.hntechserver.question.Question
import hntech.hntechserver.utils.function.isNewCheck
import org.springframework.data.domain.Page

data class QuestionPagedResponse(
    var currentPage: Int,
    var totalPage: Int,
    var questions: List<QuestionSimpleResponse>
) {
    constructor(questions: Page<Question>) : this(
        currentPage = questions.number,
        totalPage = questions.totalPages,
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
        createTime = question.createTime.split(" ")[0],
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
        createTime = question.createTime.split(" ")[0],
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


fun convertDto(questions: Page<Question>): QuestionPagedResponse {
    val questionList: ArrayList<QuestionSimpleResponse> = arrayListOf()
    questions.forEach { questionList.add(QuestionSimpleResponse(it)) }
    return QuestionPagedResponse(
        currentPage = questions.number,
        totalPage = questions.totalPages,
        questions = questionList
    )
}