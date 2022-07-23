package hntech.hntechserver.question.dto

import hntech.hntechserver.domain.Admin
import hntech.hntechserver.domain.Comment
import hntech.hntechserver.domain.Question

data class QuestionCreateForm(
    var writer: String,
    var password: Int,
    var title: String,
    var content: String
) {
    companion object {
        @JvmStatic fun toEntity(question : QuestionCreateForm): Question {
            return Question(
                writer = question.writer,
                password = question.password,
                status = "대기",
                title = question.title,
                content = question.content,
                viewCount = 0
            )
        }
    }
}

data class QuestionUpdateForm(
    var writer: String,
    var title: String,
    var content: String
)

data class CommentCreateForm(
    var content: String,
    var isAdmin: Boolean
) {
    companion object {
        @JvmStatic fun toEntity(comment: CommentCreateForm, question: Question): Comment {
            return Comment(
                question = question,
                isAdmin = comment.isAdmin,
                content = comment.content
            )
        }
    }
}

data class CommentUpdateForm(
    var content: String
)