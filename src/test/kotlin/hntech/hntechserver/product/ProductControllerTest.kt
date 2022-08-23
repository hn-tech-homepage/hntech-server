package hntech.hntechserver.product

import com.fasterxml.jackson.databind.ObjectMapper
import hntech.hntechserver.domain.category.CategoryService
import hntech.hntechserver.domain.category.CreateCategoryForm
import hntech.hntechserver.domain.file.File
import hntech.hntechserver.domain.file.FileRepository
import hntech.hntechserver.domain.file.FileService
import hntech.hntechserver.domain.product.*
import hntech.hntechserver.initTestFile
import hntech.hntechserver.setMockSession
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
internal class ProductControllerTest {

    @Autowired lateinit var mvc: MockMvc
    @Autowired lateinit var mapper: ObjectMapper
    @Autowired lateinit var fileService: FileService
    @Autowired lateinit var categoryService: CategoryService
    @Autowired lateinit var productService: ProductService
    @Autowired lateinit var productRepository: ProductRepository

    private val testFile = initTestFile()

    fun uploadFile(): File = fileService.saveFile(testFile)

    fun generateCreateForm(categoryIdx: Int, name: String, docNames: List<String>): ProductCreateForm {
        return ProductCreateForm(
            categoryName = categoryService.getAllCategories()[categoryIdx].categoryName,
            productName = name,
            description = "$name 의 설명 텍스트",
            files = UploadedFiles(
                representativeImage = uploadFile().id!!,
                productImages = listOf(uploadFile().id!!),
                standardImages = listOf(uploadFile().id!!),
                docFiles = docNames.map { DocFile(it, uploadFile().id!!) }
            )
        )
    }
    fun generateUpdateForm(name: String, docNames: List<String>): ProductUpdateForm {
        return ProductUpdateForm(
            productName = name,
            description = "$name 의 수정된 설명 텍스트",
            files = UploadedFiles(
                representativeImage = uploadFile().id!!,
                productImages = listOf(uploadFile().id!!),
                standardImages = listOf(uploadFile().id!!),
                docFiles = docNames.map { DocFile(it, uploadFile().id!!) }
            )
        )
    }

    companion object {
        @BeforeAll @JvmStatic
        fun initData(@Autowired categoryService: CategoryService,
                     @Autowired productService: ProductService,
                     @Autowired fileService: FileService
        ) {
            val testFile = initTestFile()
            categoryService.createCategory(CreateCategoryForm("카테고리"))
            repeat(3) {
                productService.createProduct(ProductCreateForm(
                    categoryName = categoryService.getAllCategories()[0].categoryName,
                    productName = "제품$it",
                    description = "제품$it 의 설명 텍스트",
                    files = UploadedFiles(
                        representativeImage = fileService.saveFile(testFile).id!!,
                        productImages = listOf(fileService.saveFile(testFile).id!!),
                        standardImages = listOf(fileService.saveFile(testFile).id!!),
                        docFiles = listOf(
                            DocFile("인증서", fileService.saveFile(testFile).id!!)
                        )
                    )
                ))
            }
        }
        @AfterAll @JvmStatic
        fun deleteResources(@Autowired fileService: FileService,
                            @Autowired fileRepository: FileRepository
        ) = fileService.deleteAllFiles(fileRepository.findAll())
    }

    @AfterEach
    fun clearProducts() = productRepository.deleteAll()

    @Test
    fun `제품 목록 조회`() {
        mvc.get("/product") {}
            .andExpect { status { isOk() } }
            .andDo { print() }
    }

    @Test
    fun `제품 상세 조회`() {
        mvc.get("/product/${productService.getAllProducts()[0].id}") {}
            .andExpect { status { isOk() } }
            .andDo { print() }
    }
    
    @Test
    fun `제품 상세 조회 실패 #없는 제품`() {
        mvc.get("/product/0") {}
            .andExpect { status { isBadRequest() } }
            .andDo { print() }
    }

    @Test
    fun `제품 생성`() {
        mvc.post("/product") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(
                generateCreateForm(0, "제품", listOf("승인서"))
            )
            session = setMockSession()
        }
            .andExpect { status { isOk() } }
            .andDo { print() }
    }
    
    @Test
    fun `제품 생성 실패 #검증 실패`() {
        mvc.post("/product") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(
                generateCreateForm(0, "", listOf())
            )
        }
            .andExpect { status { isUnauthorized() } }
            .andDo { print() }
    }

    @Test
    fun updateProduct() {
    }

    @Test
    fun updateCategorySequence() {
    }

    @Test
    fun deleteProduct() {
    }
}