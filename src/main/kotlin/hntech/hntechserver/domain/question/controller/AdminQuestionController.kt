package hntech.hntechserver.domain.question.controller

import hntech.hntechserver.auth.Auth
import hntech.hntechserver.common.BoolResponse
import hntech.hntechserver.domain.question.QuestionService
import hntech.hntechserver.domain.question.dto.QuestionDetailResponse
import hntech.hntechserver.domain.question.dto.UpdateAdminQuestionForm
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/admin/question")
//@RequestMapping("/admin/question")
class AdminQuestionController(private val questionService: QuestionService) {

    /**
     * 관리자 모드
     */
    // 문의사항 비밀번호 없이 조회
    @Auth
    @GetMapping("/{questionId}")
    fun getQuestionByAdmin(@PathVariable("questionId") id: Long): QuestionDetailResponse =
        questionService.getQuestionToDto(id)

    // 문의사항 수정
    @Auth
    @PutMapping("/{questionId}")
    fun updateQuestionByAdmin(
        @PathVariable("questionId") id: Long,
        @Valid @RequestBody form: UpdateAdminQuestionForm
    ): QuestionDetailResponse =
        questionService.updateAdminQuestion(id, form)


    // 문의사항 답변완료
    @Auth
    @PutMapping("/{questionId}/complete")
    fun updateQuestionStatus(@PathVariable("questionId") id: Long): QuestionDetailResponse =
        questionService.setStatusComplete(id)

    // 문의사항 삭제
    @Auth
    @DeleteMapping("/{questionId}")
    fun deleteQuestion(@PathVariable("questionId") id: Long): BoolResponse =
        questionService.deleteQuestion(id)
}

