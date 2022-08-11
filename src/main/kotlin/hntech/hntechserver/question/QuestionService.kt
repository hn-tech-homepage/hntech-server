package hntech.hntechserver.question

import hntech.hntechserver.comment.CommentRepository
import hntech.hntechserver.question.dto.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val commentRepository: CommentRepository,
    private val questionAlarmManager: QuestionAlarmManager
) {
    private fun getQuestion(questionId: Long): Question =
        questionRepository.findById(questionId).orElseThrow { throw QuestionException(QUESTION_NOT_FOUND) }

    private fun getQuestionByIdAndPassword(questionId: Long, password: String): Question =
        questionRepository.findByIdAndPassword(questionId, password) ?:
            throw QuestionException(QUESTION_NOT_FOUND)


    fun createQuestion(form: CreateQuestionForm): Question {
        val question = questionRepository.save(convertEntity(form))
        questionAlarmManager.addNewQuestion(question)
        return question
    }

    @Transactional(readOnly = true)
    // 전체 문의사항 페이징 조회
    fun findAllQuestions(pageable: Pageable): Page<Question> =
        questionRepository.findAll(pageable)
    
    // 자주 묻는 질문 리스트 조회
    fun findFAQ(pageable: Pageable): Page<Question> = questionRepository.findAllFAQ(pageable)

    // 작성한 비밀번호로 해당 문의사항 조회
    fun findQuestionByIdAndPassword(id: Long, password: String): QuestionCompleteResponse {
        val question = getQuestionByIdAndPassword(id, password)
        val comments = commentRepository.findAllByQuestionId(id).map { CommentResponse(it) }
        return QuestionCompleteResponse(question, comments)
    }

    // 문의사항 제목, 내용 수정
    fun updateQuestion(id: Long, form: UpdateQuestionForm): Question {
        val question = getQuestion(id)
        question.update(form.title, form.content)
        return question
    }
    
    // 문의사항 FAQ 지정 (수정)
    fun updateFAQ(id: Long, form: UpdateQuestionFAQForm): Question {
        val question = getQuestion(id)
        question.update(FAQ = form.FAQ)
        return question
    }

    // 문의사항 처리 상태 변경
    fun updateStatus(id: Long, status: String): Question {
        val question = getQuestion(id)
        question.update(status = status)
        return question
    }
    
    // 문의사항 삭제
    fun deleteQuestion(id: Long): Boolean {
        questionRepository.delete(getQuestion(id))
        return true
    }
}

