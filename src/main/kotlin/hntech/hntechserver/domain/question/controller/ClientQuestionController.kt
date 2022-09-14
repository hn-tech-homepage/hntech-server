package hntech.hntechserver.domain.question.controller

import hntech.hntechserver.common.BoolResponse
import hntech.hntechserver.domain.question.QuestionService
import hntech.hntechserver.domain.question.dto.*
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/question")
//@RequestMapping("/question")
class ClientQuestionController(private val questionService: QuestionService) {

    /**
     * 사용자 모드
     */
    // 문의사항 생성
    @PostMapping
    fun createQuestion(
        @Valid @RequestBody form: CreateQuestionForm
    ): QuestionDetailResponse = questionService.createQuestion(form)


    // 문의사항 리스트 페이징해서 조회
    @GetMapping
    fun getQuestionsByPaging(
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): QuestionPagedResponse = questionService.findAllQuestions(pageable)

    
    // 자주 묻는 질문 조회
    @GetMapping("/faq")
    fun getAllFAQ(
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): QuestionPagedResponse = questionService.findFAQ(pageable)

    // 비밀번호로 문의사항 상세 조회
    @PostMapping("/{questionId}")
    fun getQuestion(
        @PathVariable("questionId") id: Long,
        @Valid @RequestBody form: GetQuestionForm
    ): QuestionDetailResponse =
        questionService.findQuestionByIdAndPassword(id, form.password)

    // FAQ 로 설정된 문의사항 상세 조회
    @GetMapping("/faq/{questionId}")
    fun getQuestionWithNoPassword(
        @PathVariable("questionId") id: Long
    ): QuestionDetailResponse =
        questionService.getQuestionToDto(id)

    // 문의사항 수정
    @PutMapping("/{questionId}")
    fun updateQuestionForm(
        @PathVariable("questionId") id: Long,
        @Valid @RequestBody form: UpdateClientQuestionForm
    ): QuestionDetailResponse =
        questionService.updateClientQuestion(id, form)


    // 문의사항 삭제
    @DeleteMapping("/{questionId}")
    fun deleteQuestion(@PathVariable("questionId") id: Long): BoolResponse =
        questionService.deleteQuestion(id)
}

