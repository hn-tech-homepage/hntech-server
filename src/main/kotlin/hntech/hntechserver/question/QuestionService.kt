package hntech.hntechserver.question

import hntech.hntechserver.comment.CommentRepository
import hntech.hntechserver.question.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val commentRepository: CommentRepository,
    private val questionManager: QuestionManager
) {
    private fun getQuestion(questionId: Long): Question =
        questionRepository.findById(questionId).orElseThrow { throw QuestionException(QUESTION_NOT_FOUND) }

    private fun getQuestionByIdAndPassword(questionId: Long, password: String): Question =
        questionRepository.findByIdAndPassword(questionId, password).orElseThrow {
            QuestionException(QUESTION_NOT_FOUND)
        }

    @Transactional
    fun createQuestion(form: QuestionCreateForm): Question {
        val question = questionRepository.save(convertEntity(form))
        questionManager.addNewQuestion(question)
        return question
    }

    // 전체 문의사항 페이징 조회
    fun findAllQuestions(pageable: Pageable): Page<Question> =
        questionRepository.findAll(pageable)
    
    // 자주 묻는 질문 리스트 조회
    fun findFAQ(): List<Question> = questionRepository.findAllFAQ()

    // 작성한 비밀번호로 해당 문의사항 조회
    fun findQuestionByIdAndPassword(id: Long, password: String): QuestionCompleteResponse {
        val question = getQuestionByIdAndPassword(id, password)
        val comments = commentRepository.findAllByQuestionId(id).map { CommentResponse(it) }
        return QuestionCompleteResponse(question, comments)
    }

    // 문의사항 제목, 내용 수정
    @Transactional
    fun updateQuestion(id: Long, form: QuestionUpdateForm): Question {
        val question = getQuestion(id)
        question.update(form.title, form.content)
        return question
    }
    
    // 문의사항 처리 상태 수정
    @Transactional
    fun updateQuestion(id: Long, form: QuestionFAQUpdateForm): Question {
        val question = getQuestion(id)
        question.update(form.FAQ.toBoolean())
        return question
    }
    
    // 문의사항 삭제
    @Transactional
    fun deleteQuestion(id: Long) =
        questionRepository.delete(getQuestion(id))
}

