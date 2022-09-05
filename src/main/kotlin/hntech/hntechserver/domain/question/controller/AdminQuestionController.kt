package hntech.hntechserver.domain.question.controller

import hntech.hntechserver.domain.question.QuestionService
import hntech.hntechserver.domain.question.dto.QuestionDetailResponse
import hntech.hntechserver.domain.question.dto.QuestionSimpleResponse
import hntech.hntechserver.domain.question.dto.UpdateAdminQuestionForm
import hntech.hntechserver.utils.BoolResponse
import hntech.hntechserver.auth.Auth
import hntech.hntechserver.exception.ValidationException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/admin/question")
class AdminQuestionController(private val questionService: QuestionService) {

    /**
     * 관리자 모드
     */
    // 문의사항 비밀번호 없이 조회
    @Auth
    @GetMapping("/{questionId}")
    fun getQuestionByAdmin(@PathVariable("questionId") id: Long) =
        QuestionDetailResponse(questionService.getQuestion(id))

    // 문의사항 수정
    @Auth
    @PutMapping("/{questionId}")
    fun updateQuestionByAdmin(
        @PathVariable("questionId") id: Long,
        @Valid @RequestBody form: UpdateAdminQuestionForm,
        br: BindingResult
    ) = QuestionDetailResponse(questionService.updateAdminQuestion(id, form))


    // 문의사항 답변완료
    @Auth
    @PutMapping("/{questionId}/complete")
    fun updateQuestionStatus(@PathVariable("questionId") id: Long, ) =
        QuestionDetailResponse(questionService.setStatusComplete(id))

    // 문의사항 삭제
    @Auth
    @DeleteMapping("/{questionId}")
    fun deleteQuestion(@PathVariable("questionId") id: Long) =
        BoolResponse(questionService.deleteQuestion(id))
}

