package hntech.hntechserver.comment

import hntech.hntechserver.question.*
import hntech.hntechserver.question.dto.CommentCreateForm
import hntech.hntechserver.question.dto.CommentUpdateForm
import hntech.hntechserver.question.dto.convertEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CommentService(
    private val questionRepository: QuestionRepository,
    private val commentRepository: CommentRepository,
    private val questionManager: QuestionManager
) {
    private fun getQuestion(questionId: Long): Question =
        questionRepository.findById(questionId).orElseThrow { throw QuestionException(QUESTION_NOT_FOUND) }

    fun createComment(questionId: Long, form: CommentCreateForm): Comment {
        val question = getQuestion(questionId)
        val comment = commentRepository.save(convertEntity(form, question))
//        question.addComment(comment)
        // 클라이언트가 새 댓글 등록 시 메일로 보낼 문의사항 리스트에 추가
        if (form.writer != "관리자")
            questionManager.addNewCommentQuestion(question)
        return comment
    }

    @Transactional(readOnly = true)
    fun getComment(commentId: Long): Comment =
        commentRepository.findById(commentId).orElseThrow { throw CommentException(COMMENT_NOT_FOUND) }

    fun updateComment(commentId: Long, form: CommentUpdateForm): Comment {
        val comment = getComment(commentId)
        comment.update(form.content)
        return comment
    }

    fun deleteComment(commentId: Long) {
        val comment = getComment(commentId)
        commentRepository.delete(comment)
    }
}