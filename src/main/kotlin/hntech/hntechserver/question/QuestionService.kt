package hntech.hntechserver.question

import hntech.hntechserver.question.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val commentRepository: CommentRepository
) {

    @Transactional
    fun createQuestion(question: QuestionCreateForm): Question {
        return questionRepository.save(convertEntity(question))
    }

    // 전체 문의사항 페이징해서 간략 포맷 반환
    fun findAllQuestions(pageable: Pageable): Page<Question> {
        return questionRepository.findAll(pageable)
    }

    // 작성한 비밀번호로 해당 문의사항 조회
    fun findQuestionByIdAndPassword(id: Long, password: String): QuestionCompleteResponse {
        val question = questionRepository.findByIdAndPassword(id, password).orElseThrow { throw NoSuchElementException("해당 문의사항을 찾을 수 없습니다.") }
        val comments = commentRepository.findAllByQuestionId(id).map { convertDto(it) }
        return convertDto(question, comments)
    }

    // 문의사항 제목, 내용 수정
    @Transactional
    fun updateQuestion(id: Long, form: QuestionUpdateForm): Question {
        val question = questionRepository.findById(id).orElseThrow { throw NoSuchElementException("해당 문의사항을 찾을 수 없습니다.") }
        question.update(form.title, form.content)
        return question
    }
    
    // 문의사항 처리 상태 수정
    @Transactional
    fun updateQuestion(id: Long, form: QuestionStatusUpdateForm): Question {
        val question = questionRepository.findById(id).orElseThrow { throw NoSuchElementException("해당 문의사항을 찾을 수 없습니다.") }
        question.update(form.status)
        return question
    }
    
    // 문의사항 삭제
    @Transactional
    fun deleteQuestion(id: Long) = questionRepository.deleteById(id)
}

@Service
class CommentService(
    private val questionRepository: QuestionRepository,
    private val commentRepository: CommentRepository
) {

    @Transactional
    fun createComment(questionId: Long, form: CommentCreateForm): Comment {
        val question = questionRepository.findById(questionId).orElseThrow { throw NoSuchElementException("해당 문의사항이 없습니다.") }
        val comment = commentRepository.save(convertEntity(form, question))
        question.addComment(comment)
        return comment
    }

    @Transactional
    fun updateComment(commentId: Long, form: CommentUpdateForm): Comment {
        val comment = commentRepository.findById(commentId).orElseThrow { throw NoSuchElementException("해당 댓글이 없습니다.") }
        comment.update(form.content)
        return comment
    }

    @Transactional
    fun deleteComment(commentId: Long) = commentRepository.deleteById(commentId)
}