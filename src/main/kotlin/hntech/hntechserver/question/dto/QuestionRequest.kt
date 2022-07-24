package hntech.hntechserver.question.dto

import hntech.hntechserver.question.Comment
import hntech.hntechserver.question.Question

data class QuestionCreateForm(
    var writer: String,
    var password: String,
    var title: String,
    var content: String
)

data class QuestionUpdateForm(
    var writer: String,
    var title: String,
    var content: String
)

data class CommentCreateForm(
    var content: String,
    var isAdmin: Boolean
)

data class CommentUpdateForm(
    var content: String
)

fun convertEntity(question : QuestionCreateForm): Question {
    return Question(
        writer = question.writer,
        password = question.password,
        status = "대기",
        title = question.title,
        content = question.content,
    )
}
fun convertEntity(comment: CommentCreateForm, question: Question): Comment {
    return Comment(
        question = question,
        isAdmin = comment.isAdmin,
        content = comment.content
    )
}