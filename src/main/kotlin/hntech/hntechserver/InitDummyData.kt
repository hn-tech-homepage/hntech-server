package hntech.hntechserver

import hntech.hntechserver.domain.admin.AdminService
import hntech.hntechserver.domain.archive.ArchiveForm
import hntech.hntechserver.domain.archive.ArchiveService
import hntech.hntechserver.domain.category.CategoryService
import hntech.hntechserver.domain.category.CreateCategoryForm
import hntech.hntechserver.domain.comment.CommentService
import hntech.hntechserver.domain.file.File
import hntech.hntechserver.domain.file.FileRepository
import hntech.hntechserver.domain.question.QuestionService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import javax.annotation.PostConstruct

@Component
@Transactional
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


//        // 문의사항 세팅
//        repeat(30) {
//            questionService.createQuestion(
//                CreateQuestionForm(
//                    writer = "user$it",
//                    password = "1234",
//                    title = "user$it 의 문의사항",
//                    content = "문의사항 내용.."
//                )
//            )
//        }
//        // FAQ 세팅
//        repeat(10) {
//            questionService.updateAdminQuestion((it + 1).toLong(), UpdateAdminQuestionForm("제목", "내용", "true"))
//        }
//
//        // 댓글 세팅
//        commentService.createComment(1L, CreateCommentForm("전예진", "나는 바보입니다"))
//        commentService.createComment(1L, CreateCommentForm("전예진", "나는 바보입니다2"))
//        commentService.createComment(1L, CreateCommentForm("관리자", "알고있습니다."))
//
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