package hntech.hntechserver.question

import hntech.hntechserver.question.dto.CommentCreateForm
import hntech.hntechserver.question.dto.CommentUpdateForm
import hntech.hntechserver.question.dto.QuestionCreateForm
import hntech.hntechserver.question.dto.QuestionUpdateForm
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class QuestionService(private val questionRepository: QuestionRepository) {

    fun createQuestion(question: QuestionCreateForm): Question {
        return questionRepository.save(QuestionCreateForm.toEntity(question))
    }

    // 전체 문의사항 페이징해서 간략 포맷 반환
    fun findAllQuestions(pageable: Pageable): Page<Question> {
        return questionRepository.findAll(pageable)
    }

    // 작성한 비밀번호로 해당 문의사항 조회
    fun findQuestionByIdAndPassword(id: Long, password: String): Question {
        return questionRepository.findByIdAndPassword(id, password) ?: throw NoSuchElementException("해당 페이지를 찾을 수 없습니다.")
    }

    // 문의사항 수정
    fun updateQuestion(id: Long, form: QuestionUpdateForm): Question {
        val question = questionRepository.findById(id).orElseThrow { throw NoSuchElementException("해당 문의사항을 찾을 수 없습니다.") }
        var (writer, title, content) = form
        question.writer = writer
        question.title = title
        question.content = content
        return question
    }
    
    // 문의사항 삭제
    fun deleteQuestion(id: Long) = questionRepository.deleteById(id)
}

@Service
class CommentService(
    private val questionRepository: QuestionRepository,
    private val commentRepository: CommentRepository
) {

    fun createComment(questionId: Long, form: CommentCreateForm): Comment {
        val question = questionRepository.findById(questionId).orElseThrow { throw NoSuchElementException("해당 문의사항이 없습니다.") }
        return commentRepository.save(CommentCreateForm.toEntity(form, question))
    }

    fun updateComment(questionId: Long, commentId: Long, form: CommentUpdateForm): Comment {
        val comment = commentRepository.findById(commentId).orElseThrow { throw NoSuchElementException("해당 댓글이 없습니다.") }
        comment.content = form.content
        return comment
    }

    fun deleteComment(questionId: Long, commentId: Long) = commentRepository.deleteById(commentId)
}