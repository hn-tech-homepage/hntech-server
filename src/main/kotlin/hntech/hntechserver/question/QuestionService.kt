package hntech.hntechserver.question

import hntech.hntechserver.question.dto.CreateQuestionForm
import hntech.hntechserver.question.dto.UpdateAdminQuestionForm
import hntech.hntechserver.question.dto.UpdateClientQuestionForm
import hntech.hntechserver.question.dto.convertEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class QuestionService(
    private val questionRepository: QuestionRepository,
    private val questionAlarmManager: QuestionAlarmManager
) {

    @Transactional(readOnly = true)
    fun getQuestion(questionId: Long): Question =
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
    fun findQuestionByIdAndPassword(id: Long, password: String): Question =
        getQuestionByIdAndPassword(id, password)


    // 문의사항 제목, 내용 수정
    fun updateClientQuestion(id: Long, form: UpdateClientQuestionForm): Question {
        val question = getQuestion(id)
        question.update(title = form.title, content = form.content)
        return question
    }

    fun updateAdminQuestion(id: Long, form: UpdateAdminQuestionForm): Question {
        val question = getQuestion(id)
        question.update(title = form.title, content = form.content, FAQ = form.FAQ)
        return question
    }

    // 문의사항 처리 상태 변경
    fun setStatusComplete(id: Long): Question {
        val question = getQuestion(id)
        question.update(status = "완료")
        return question
    }
    
    // 문의사항 삭제
    fun deleteQuestion(id: Long): Boolean {
        questionRepository.delete(getQuestion(id))
        return true
    }
}

