package hntech.hntechserver.question

// 문의사항
const val QUESTION_NOT_FOUND = "해당 문의사항을 찾을 수 없습니다."

// 댓글
const val COMMENT_NOT_FOUND = "해당 댓글을 찾을 수 없습니다."

class QuestionException(message: String): RuntimeException(message)
class CommentException(message: String): RuntimeException(message)