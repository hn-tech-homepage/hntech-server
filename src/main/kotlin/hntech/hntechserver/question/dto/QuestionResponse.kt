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
    var status: String,
    var createTime: LocalDateTime,
    var updateTime: LocalDateTime
)

data class QuestionDetailResponse(
    var id: Long,
    var writer: String,
    var password: String,
    var status: String,
    var title: String,
    var content: String,
    var createTime: LocalDateTime,
    var updateTime: LocalDateTime
)

data class QuestionCompleteResponse(
    var id: Long,
    var writer: String,
    var password: String,
    var status: String,
    var title: String,
    var content: String,
    var comments: List<CommentResponse>,
    var createTime: LocalDateTime,
    var updateTime: LocalDateTime
)

data class CommentResponse(
    var id: Long,
    var writer: String,
    var sequence: Int,
    var content: String
)

fun convertDto(questions: Page<Question>): QuestionPagedResponse {
    var questionList: ArrayList<QuestionSimpleResponse> = arrayListOf()
    questions.forEach { question ->
        questionList.add(convertDto(question, false))
    }
    return QuestionPagedResponse(
        currentPage = questions.number,
        totalPage = questions.totalPages,
        questions = questionList
    )
}
fun <T> convertDto(question: Question, isDetail: Boolean): T {
    if (isDetail) {
        return QuestionDetailResponse(
            id = question.id!!,
            writer = question.writer,
            password = question.password,
            status = question.status,
            title = question.title,
            content = question.content,
            createTime = question.createTime,
            updateTime = question.updateTime
        ) as T
    }
    else {
        return QuestionSimpleResponse(
            id = question.id!!,
            writer = question.writer,
            title = question.title,
            status = question.status,
            createTime = question.createTime,
            updateTime = question.updateTime
        ) as T
    }
}
fun convertDto(question: Question, comments: List<CommentResponse>): QuestionCompleteResponse {
    return QuestionCompleteResponse(
        id = question.id!!,
        writer = question.writer,
        password = question.password,
        status = question.status,
        title = question.title,
        content = question.content,
        comments = comments,
        createTime = question.createTime,
        updateTime = question.updateTime
    )
}
fun convertDto(comment: Comment): CommentResponse {
    return CommentResponse(
        id = comment.id!!,
        writer = comment.writer,
        sequence = comment.sequence,
        content = comment.content
    )
}