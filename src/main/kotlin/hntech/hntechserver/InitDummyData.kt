package hntech.hntechserver

import hntech.hntechserver.question.QuestionService
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InitDummyData {

    @Bean
    fun databaseInitializer(questionService: QuestionService) = ApplicationRunner {

        // 문의사항 Init
//        repeat(30) {
//            questionService.createQuestion(QuestionCreateForm(
//                writer = "user$it",
//                password = "1234",
//                title = "user$it 의 문의사항",
//                content = "문의사항 내용.."
//            ))
//        }
//        repeat(10) {
//            questionService.updateQuestion((it + 1).toLong(), QuestionFAQUpdateForm("true"))
//        }
    }
}