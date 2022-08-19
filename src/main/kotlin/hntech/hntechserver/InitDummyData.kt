package hntech.hntechserver

import hntech.hntechserver.admin.AdminService
import hntech.hntechserver.category.CategoryService
import hntech.hntechserver.category.CreateCategoryForm
import hntech.hntechserver.question.QuestionService
import hntech.hntechserver.question.dto.CreateQuestionForm
import hntech.hntechserver.question.dto.UpdateQuestionFAQForm
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InitDummyData {

    @Bean
    fun questionInitializer(questionService: QuestionService) =
        ApplicationRunner {
        // 문의사항 더미데이터
        repeat(30) {
            questionService.createQuestion(
                CreateQuestionForm(
                writer = "user$it",
                password = "1234",
                title = "user$it 의 문의사항",
                content = "문의사항 내용.."
            )
            )
        }
        // FAQ 더미데이터
        repeat(10) {
            questionService.updateFAQ((it + 1).toLong(), UpdateQuestionFAQForm("true"))
        }
    }

    @Bean
    fun adminInitializer(adminService: AdminService) =
        ApplicationRunner {
            adminService.createAdmin("1234")
        }

    @Bean
    fun categoryInitializer(categoryService: CategoryService) =
        ApplicationRunner {
            categoryService.createCategory(CreateCategoryForm("스프링클러"))
            categoryService.createCategory(CreateCategoryForm("일반자료"))
            categoryService.createCategory(CreateCategoryForm("신축배관"))
            categoryService.createCategory(CreateCategoryForm("제품승인서"))
        }
}