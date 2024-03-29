package hntech.hntechserver.domain.question.controller

import hntech.hntechserver.common.BoolResponse
import hntech.hntechserver.common.PAGE_SIZE
import hntech.hntechserver.domain.question.QuestionService
import hntech.hntechserver.domain.question.dto.*
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/question")
class ClientQuestionController(private val questionService: QuestionService) {

    /**
     * 사용자 모드
     */
    // 문의사항 생성
    @PostMapping
    fun createQuestion(
        @Valid @ModelAttribute form: CreateQuestionForm
    ): QuestionDetailResponse = questionService.createQuestion(form)

    // 문의사항 리스트 페이징해서 조회
    @GetMapping
    fun getQuestionsByPaging(
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC, size = PAGE_SIZE) pageable: Pageable
    ): QuestionPagedResponse = questionService.findAllQuestions(pageable)
    
    // 자주 묻는 질문 조회
    @GetMapping("/faq")
    fun getAllFAQ(
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC, size = PAGE_SIZE) pageable: Pageable
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
    fun getQuestionWithNoPassword(@PathVariable("questionId") id: Long): QuestionDetailResponse =
        questionService.getQuestionToDto(id)

    // 문의사항 수정
    @PutMapping("/{questionId}")
    fun updateQuestionForm(
        @PathVariable("questionId") id: Long,
        @Valid @ModelAttribute form: UpdateClientQuestionForm
    ): QuestionDetailResponse =
        questionService.updateClientQuestion(id, form)


    // 문의사항 삭제
    @DeleteMapping("/{questionId}")
    fun deleteQuestion(@PathVariable("questionId") id: Long): BoolResponse =
        questionService.deleteQuestion(id)

    // 첨부 이미지 파일 삭제
    @DeleteMapping("/{questionId}/file/{fileId}")
    fun deleteAttachedFile(@PathVariable("questionId") questionId: Long,
                           @PathVariable("fileId") fileId: Long
    ) = questionService.deleteAttachedFile(questionId, fileId)
}

