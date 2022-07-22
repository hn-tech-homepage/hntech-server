package hntech.hntechserver.question.dto

import hntech.hntechserver.domain.Comment
import hntech.hntechserver.domain.Question
import java.time.LocalDateTime

data class QuestionPagedResponse(
    var currentPage : Int,
    var totalPage : Int,
    var currentPageCount : Int,
    var totalCount : Int,
    var questions : List<QuestionSimpleResponse>
)

data class QuestionSimpleResponse(
    var id : Long?,
    var title : String,
    var status : String,
    var viewCount: Int,
    var createTime : LocalDateTime,
    var updateTime : LocalDateTime
)

data class QuestionDetailResponse(
    var id: Long?,
    var writer: String,
    var password: Int,
    var status: String,
    var comments: MutableSet<Comment>,
    var title: String,
    var content: String,
    var viewCount: Int,
    var createTime : LocalDateTime,
    var updateTime : LocalDateTime
)

class Converter {
    companion object {
        @JvmStatic fun toQuestionSimpleResponse(question: Question) : QuestionSimpleResponse {
            return QuestionSimpleResponse(
                question.id,
                question.title,
                question.status,
                question.viewCount,
                question.createTime,
                question.updateTime
            )
        }
        @JvmStatic fun toQuestionDetailResponse(question: Question) : QuestionDetailResponse {
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