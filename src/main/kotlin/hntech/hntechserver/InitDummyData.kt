package hntech.hntechserver

import hntech.hntechserver.admin.AdminService
import hntech.hntechserver.archive.ArchiveForm
import hntech.hntechserver.archive.ArchiveService
import hntech.hntechserver.category.CategoryService
import hntech.hntechserver.category.CreateCategoryForm
import hntech.hntechserver.comment.CommentService
import hntech.hntechserver.comment.CreateCommentForm
import hntech.hntechserver.file.File
import hntech.hntechserver.file.FileRepository
import hntech.hntechserver.question.QuestionService
import hntech.hntechserver.question.dto.CreateQuestionForm
import hntech.hntechserver.question.dto.UpdateQuestionFAQForm
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class InitDummyData(
    private val fileRepository: FileRepository,
    private val adminService: AdminService,
    private val categoryService: CategoryService,
    private val questionService: QuestionService,
    private val archiveService: ArchiveService,
    private val commentService: CommentService
) {

    @PostConstruct
    fun initDummyData() {
        // 어드민 세팅
        adminService.createAdmin("1234")

        // 파일 세팅
        repeat(3) {
            fileRepository.save(File(originalFilename = "test$it", serverFilename = "test$it.jpg"))
        }

        // 카테고리 세팅
        categoryService.createCategory(CreateCategoryForm("스프링클러"))
        categoryService.createCategory(CreateCategoryForm("일반자료"))
        categoryService.createCategory(CreateCategoryForm("신축배관"))
        categoryService.createCategory(CreateCategoryForm("제품승인서"))


        // 문의사항 세팅
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
        // FAQ 세팅
        repeat(10) {
            questionService.updateFAQ((it + 1).toLong(), UpdateQuestionFAQForm("true"))
        }

        // 댓글 세팅
        commentService.createComment(1L, CreateCommentForm("전예진", "나는 바보입니다"))
        commentService.createComment(1L, CreateCommentForm("전예진", "나는 바보입니다2"))
        commentService.createComment(1L, CreateCommentForm("관리자", "알고있습니다."))

        // 자료실 세팅
        val files = listOf("test0.jpg", "test1.jpg", "test2.jpg")
        val form = ArchiveForm("테스트", "스프링클러", "false", "내용", files)
        repeat(30) {
            archiveService.createArchive(form)
        }
        val form2 = ArchiveForm("공지사항", "일반자료", "true", "전예진", files)
        repeat(10) {
            archiveService.createArchive(form2)
        }


    }
}