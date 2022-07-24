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
    var id: Long?,
    var writer: String,
    var title: String,
    var status: String,
    var createTime: LocalDateTime,
    var updateTime: LocalDateTime
)

data class QuestionDetailResponse(
    var id: Long?,
    var writer: String,
    var password: String,
    var status: String,
    var comments: MutableSet<Comment>,
    var title: String,
    var content: String,
    var createTime: LocalDateTime,
    var updateTime: LocalDateTime
)

data class CommentResponse(
    var id: Long?,
    var question: Question,
    var isAdmin: Boolean,
    var sequence: Int,
    var content: String
)

fun convertDto(questions: Page<Question>): QuestionPagedResponse {
    var questionList: ArrayList<QuestionSimpleResponse> = arrayListOf()
    questions.forEach { question ->
        questionList.add(convertDto(question, false))
    }
    return QuestionPagedResponse(
        questions.number,
        questions.totalPages,
        questionList
    )
}
fun <T> convertDto(question: Question, isDetail: Boolean): T {
    if (isDetail) {
        return QuestionDetailResponse(
            question.id,
            question.writer,
            question.password,
            question.status,
            question.comments,
            question.title,
            question.content,
            question.createTime,
            question.updateTime
        ) as T
    }
    else {
        return QuestionSimpleResponse(
            question.id,
            question.writer,
            question.title,
            question.status,
            question.createTime,
            question.updateTime
        ) as T
    }
}
fun convertDto(comment: Comment): CommentResponse {
    return CommentResponse(
        comment.id,
        comment.question,
        comment.isAdmin,
        comment.sequence,
        comment.content
    )
}