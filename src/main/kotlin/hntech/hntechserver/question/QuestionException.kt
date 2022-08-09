package hntech.hntechserver.question

// 문의사항
const val QUESTION_NOT_FOUND = "해당 문의사항을 찾을 수 없습니다."

// 댓글
const val COMMENT_NOT_FOUND = "해당 댓글을 찾을 수 없습니다."

// 메일
const val EMAIL_SEND_ERROR = "메일을 전송하는데 실패했습니다."

class QuestionException(message: String): RuntimeException(message)
class CommentException(message: String): RuntimeException(message)
class EmailException(message: String): RuntimeException(message)