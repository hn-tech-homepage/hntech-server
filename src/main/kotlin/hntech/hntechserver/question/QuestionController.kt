package hntech.hntechserver.question

import hntech.hntechserver.question.dto.*
import hntech.hntechserver.utils.BoolResponse
import hntech.hntechserver.utils.auth.Auth
import hntech.hntechserver.utils.exception.ValidationException
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/question")
class QuestionController(private val questionService: QuestionService) {

    /**
     * 사용자 모드
     */
    // 문의사항 생성
    @PostMapping
    fun createQuestion(
        @Valid @RequestBody form: CreateQuestionForm,
        br: BindingResult
    ): QuestionDetailResponse {
        if (br.hasErrors()) throw ValidationException(br)
        return QuestionDetailResponse(questionService.createQuestion(form))
    }

    // 문의사항 리스트 페이징해서 조회
    @GetMapping
    fun getAllQuestions(
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ) = QuestionPagedResponse(questionService.findAllQuestions(pageable))

    
    // 자주 묻는 질문 조회
    @GetMapping("/faq")
    fun getFAQ(
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ) = QuestionPagedResponse(questionService.findFAQ(pageable))

    // 비밀번호로 문의사항 상세 조회
    @PostMapping("/{questionId}")
    fun getQuestion(
        @PathVariable("questionId") id: Long,
        @Valid @RequestBody form: GetQuestionForm,
        br: BindingResult
    ): QuestionCompleteResponse {
        if (br.hasErrors()) throw ValidationException(br)
        return questionService.findQuestionByIdAndPassword(id, form.password)
    }

    // 문의사항 제목, 내용 수정
    @PutMapping("/{questionId}")
    fun updateQuestionForm(
        @PathVariable("questionId") id: Long,
        @Valid @RequestBody form: UpdateQuestionForm,
        br: BindingResult
    ): QuestionDetailResponse {
        if (br.hasErrors()) throw ValidationException(br)
        return QuestionDetailResponse(questionService.updateQuestion(id, form))
    }

    /**
     * 관리자 모드
     */
    // 자주 묻는 질문 설정
    @Auth
    @PutMapping("/{questionId}/faq")
    fun updateFAQ(
        @PathVariable("questionId") id: Long,
        @Valid @RequestBody form: UpdateQuestionFAQForm,
        br: BindingResult
    ): QuestionSimpleResponse {
        if (br.hasErrors()) throw ValidationException(br)
        return QuestionSimpleResponse(questionService.updateFAQ(id, form))
    }

    @Auth
    @PutMapping("/{questionId}/status")
    fun updateStatus(
        @PathVariable("questionId") id: Long,
        @RequestBody form: UpdateQuestionStatusForm
    ) =
        QuestionDetailResponse(questionService.updateStatus(id, form.status))


    @Auth
    @DeleteMapping("/{questionId}")
    fun deleteQuestion(@PathVariable("questionId") id: Long) =
        BoolResponse(questionService.deleteQuestion(id))
}

