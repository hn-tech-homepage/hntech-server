package hntech.hntechserver.question.dto

import hntech.hntechserver.domain.Comment
import hntech.hntechserver.domain.Question
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

data class QuestionPagedResponse(
    var currentPage: Int,
    var totalPage: Int,
    var questions: List<QuestionSimpleResponse>
) {
    companion object {
        @JvmStatic fun toQuestionPagedResponse(questions: Page<Question>): QuestionPagedResponse {
            var questionList: ArrayList<QuestionSimpleResponse> = arrayListOf()
            questions.forEach { question ->
                questionList.add(QuestionSimpleResponse.toQuestionSimpleResponse(question))
            }
            return QuestionPagedResponse(
                questions.number,
                questions.totalPages,
                questionList
            )
        }
    }
}

data class QuestionSimpleResponse(
    var id: Long?,
    var title: String,
    var status: String,
    var viewCount: Int,
    var createTime: LocalDateTime,
    var updateTime: LocalDateTime
) {
    companion object {
        @JvmStatic fun toQuestionSimpleResponse(question: Question): QuestionSimpleResponse {
            return QuestionSimpleResponse(
                question.id,
                question.title,
                question.status,
                question.viewCount,
                question.createTime,
                question.updateTime
            )
        }
    }
}

data class QuestionDetailResponse(
    var id: Long?,
    var writer: String,
    var password: Int,
    var status: String,
    var comments: MutableSet<Comment>,
    var title: String,
    var content: String,
    var viewCount: Int,
    var createTime: LocalDateTime,
    var updateTime: LocalDateTime
) {
    companion object {
        @JvmStatic fun toQuestionDetailResponse(question: Question): QuestionDetailResponse {
            return QuestionDetailResponse(
                question.id,
                question.writer,
                question.password,
                question.status,
                question.comments,
                question.title,
                question.content,
                question.viewCount,
                question.createTime,
                question.updateTime
            )
        }
    }
}

data class CommentResponse(
    var id: Long?,
    var question: Question,
    var isAdmin: Boolean,
    var sequence: Int,
    var content: String
) {
    companion object {
        @JvmStatic fun toCommentResponse(comment: Comment): CommentResponse {
            return CommentResponse(
                comment.id,
                comment.question,
                comment.isAdmin,
                comment.sequence,
                comment.content
            )
        }
    }
}