package hntech.hntechserver.question

import hntech.hntechserver.question.dto.*
import hntech.hntechserver.utils.auth.Auth
import hntech.hntechserver.utils.config.PAGE_SIZE
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
    fun createQuestion(@Valid @RequestBody form: QuestionCreateForm,
                       bindingResult: BindingResult
    ): QuestionDetailResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return QuestionDetailResponse(questionService.createQuestion(form))
    }

    // 문의사항 리스트 페이징해서 조회
    @GetMapping
    fun getAllQuestions(
        @PageableDefault(size = PAGE_SIZE, sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): QuestionPagedResponse {
        return convertDto(questionService.findAllQuestions(pageable))
    }
    
    // 자주 묻는 질문 조회
    @GetMapping("/faq")
    fun getFAQ(): List<QuestionSimpleResponse> =
        questionService.findFAQ().map { QuestionSimpleResponse(it) }

    // 비밀번호로 문의사항 상세 조회
    @PostMapping("/{questionId}")
    fun getQuestion(@PathVariable("questionId") id: Long,
                    @Valid @RequestBody form: QuestionFindForm,
                    bindingResult: BindingResult
    ): QuestionCompleteResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return questionService.findQuestionByIdAndPassword(id, form.password)
    }

    // 문의사항 제목, 내용 수정
    @PutMapping("/{questionId}")
    fun updateQuestionForm(@PathVariable("questionId") id: Long,
                           @Valid @RequestBody form: QuestionUpdateForm,
                           bindingResult: BindingResult
    ): QuestionDetailResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return QuestionDetailResponse(questionService.updateQuestion(id, form))
    }

    /**
     * 관리자 모드
     */
    // 자주 묻는 질문 설정
    @Auth
    @PatchMapping("/{questionId}")
    fun updateQuestionStatus(@PathVariable("questionId") id: Long,
                             @Valid @RequestBody form: QuestionFAQUpdateForm,
                             bindingResult: BindingResult
    ): QuestionSimpleResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return QuestionSimpleResponse(questionService.updateQuestion(id, form))
    }

    @Auth
    @DeleteMapping("/{questionId}")
    fun deleteQuestion(@PathVariable("questionId") id: Long) = questionService.deleteQuestion(id)
}

