package hntech.hntechserver

import hntech.hntechserver.common.PRODUCT
import hntech.hntechserver.domain.admin.Admin
import hntech.hntechserver.domain.admin.AdminException
import hntech.hntechserver.domain.admin.AdminService
import hntech.hntechserver.domain.archive.ArchiveForm
import hntech.hntechserver.domain.archive.ArchiveService
import hntech.hntechserver.domain.category.CategoryService
import hntech.hntechserver.domain.category.CreateCategoryForm
import hntech.hntechserver.domain.file.File
import hntech.hntechserver.domain.file.FileRepository
import hntech.hntechserver.domain.product.ProductRequestForm
import hntech.hntechserver.domain.product.ProductService
import hntech.hntechserver.domain.question.QuestionService
import hntech.hntechserver.domain.question.dto.CreateQuestionForm
import hntech.hntechserver.domain.question.dto.UpdateAdminQuestionForm
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
    private val productService: ProductService,
) {

    @PostConstruct
    fun initDummyData() {
        // 파일 세팅
        repeat(3) {
            fileRepository.save(File(originalFilename = "test$it", serverFilename = "test$it.jpg"))
        }

        // 어드민 세팅
        val admin = Admin(
            password = "1234",
            logoImage = "test0.png",

            emailSendingTime = "12",
            sendEmailAccount = "eodrmfdl1004@naver.com",
            sendEmailPassword = "Bh920506!!",
            receiveEmailAccount = "mopil1102@naver.com",

            address = "주소주소주소",
            afterService = "000-000-0000",
            phone = "031-337-4005",
            fax = "031-337-4006"
        )
        try {
            adminService.createAdmin(admin)
        } catch (_: AdminException) {}

        // 카테고리 세팅
        categoryService.createCategory(CreateCategoryForm(categoryName = "스프링클러", type = PRODUCT))
        categoryService.createCategory(CreateCategoryForm("일반자료"))
        categoryService.createCategory(CreateCategoryForm(categoryName = "신축배관", type = PRODUCT))
        categoryService.createCategory(CreateCategoryForm("제품승인서"))
        
        // 문의사항 세팅
        repeat(5) {
            questionService.createQuestion(
                CreateQuestionForm(
                    writer = "사용자$it",
                    password = "1234",
                    title = "사용자${it}의 문의사항 제목",
                    content = "사용자${it}의 문의사항 내용"
                )
            )
        }
        // FAQ 세팅
        repeat(2) {
            questionService.updateAdminQuestion((it + 1).toLong(), UpdateAdminQuestionForm("제목", "내용", "true"))
        }

        // 자료실 세팅
        repeat(5) {
            archiveService.createArchive(ArchiveForm("자료실 제목$it", "스프링클러", "false", "내용$it", null))
        }
        repeat(2) {
            archiveService.createArchive(ArchiveForm("공지사항$it", "일반자료", "true", "공지사항 내용$it", null))
        }


        // 제품 세팅
        repeat(5) {
            productService.createProduct(
                ProductRequestForm(
                    "스프링클러",
                    "스프링클러 신상$it",
                    "스프링클러 신상입니다$it",
                    null,
                    null,
                    null,
                    null)
            )
        }



    }
}