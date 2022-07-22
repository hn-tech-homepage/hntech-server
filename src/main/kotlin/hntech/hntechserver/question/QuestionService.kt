package hntech.hntechserver.question

import hntech.hntechserver.domain.Question
import hntech.hntechserver.question.dto.Converter
import hntech.hntechserver.question.dto.QuestionPagedResponse
import hntech.hntechserver.question.dto.QuestionSimpleResponse
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class QuestionService(private val questionRepository: QuestionRepository) {

    // 전체 문의사항 페이징해서 간략 포맷 반환
    fun findAllQuestions(pageable: Pageable) : QuestionPagedResponse {
        val questions = questionRepository.findAllOrderById(pageable)
        var questionList : ArrayList<QuestionSimpleResponse> = arrayListOf()
        questions.forEach { question -> questionList.add(Converter.toQuestionSimpleResponse(question)) }
        return QuestionPagedResponse(
            pageable.pageNumber,
            questions.totalPages,
            questions.size,
            questionRepository.count().toInt(),
            questionList
        )
    }

    // 작성한 비밀번호로 해당 문의사항 조회
    fun findQuestionByIdAndPassword(id : Long, password : String) : Question {
        return questionRepository.findByIdAndPassword(id, password) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "This article does not exist")
    }
}