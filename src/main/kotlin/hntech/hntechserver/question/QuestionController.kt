package hntech.hntechserver.question

import hntech.hntechserver.utils.error.ValidationException
import hntech.hntechserver.question.dto.*
import hntech.hntechserver.utils.config.QUESTION_PAGE_SIZE
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/question")
class QuestionController(private val questionService: QuestionService) {

    // 문의사항 생성
    @PostMapping
    fun createQuestion(@Valid @RequestBody question: QuestionCreateForm,
                       bindingResult: BindingResult
    ): QuestionDetailResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return convertDto(questionService.createQuestion(question), true)
    }

    // 문의사항 리스트 페이징해서 조회
    @GetMapping
    fun getAllQuestions(
        @PageableDefault(size = QUESTION_PAGE_SIZE, sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): QuestionPagedResponse {
        return convertDto(questionService.findAllQuestions(pageable))
    }

    // 비밀번호로 문의사항 상세 조회
    @PostMapping("/{question_id}")
    fun getQuestion(@PathVariable("question_id") id: Long,
                    @Valid @RequestBody form: QuestionFindForm,
                    bindingResult: BindingResult
    ): QuestionCompleteResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return questionService.findQuestionByIdAndPassword(id, form.password)
    }

    // 문의사항 제목, 내용 수정
    @PutMapping("/{question_id}")
    fun updateQuestionForm(@PathVariable("question_id") id: Long,
                           @Valid @RequestBody form: QuestionUpdateForm,
                           bindingResult: BindingResult
    ): QuestionDetailResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return convertDto(questionService.updateQuestion(id, form), true)
    }

    /**
     * 관리자
     */
    // 문의사항 처리 상태 수정
    @PatchMapping("/{question_id}")
    fun updateQuestionStatus(@PathVariable("question_id") id: Long,
                             @Valid @RequestBody form: QuestionStatusUpdateForm,
                             bindingResult: BindingResult
    ): QuestionSimpleResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return convertDto(questionService.updateQuestion(id, form), false)
    }

    @DeleteMapping("/{question_id}")
    fun deleteQuestion(@PathVariable("question_id") id: Long) = questionService.deleteQuestion(id)
}

@RestController
@RequestMapping("/comment")
class CommentController(private val commentService: CommentService) {

    @PostMapping("/{question_id}")
    fun createComment(@PathVariable("question_id") questionId: Long,
                      @Valid @RequestBody form: CommentCreateForm,
                      bindingResult: BindingResult
    ): CommentResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return convertDto(commentService.createComment(questionId, form))
    }

    @PutMapping("/{comment_id}")
    fun updateComment(@PathVariable("comment_id") commentId: Long,
                      @Valid @RequestBody form: CommentUpdateForm,
                      bindingResult: BindingResult
    ): CommentResponse {
        if (bindingResult.hasErrors()) throw ValidationException(bindingResult)
        return convertDto(commentService.updateComment(commentId, form))
    }

    @DeleteMapping("/{comment_id}")
    fun deleteComment(@PathVariable("comment_id") commentId: Long) =
        commentService.deleteComment(commentId)
}