package hntech.hntechserver.question

import hntech.hntechserver.utils.logger
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/question")
class QuestionController(private val questionService: QuestionService) {
    val log = logger()

    @GetMapping
    fun getAllQuestions(@PageableDefault(size = 10, sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable)
        = questionService.findAllQuestions(pageable)

    @PostMapping("/question/{question_id}")
    fun getQuestion(@PathVariable id : Long, password : String)
        = questionService.findQuestionByIdAndPassword(id, password)
}