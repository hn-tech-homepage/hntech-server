package hntech.hntechserver.archive

import com.fasterxml.jackson.databind.ObjectMapper
import hntech.hntechserver.domain.archive.Archive
import hntech.hntechserver.domain.archive.ArchiveForm
import hntech.hntechserver.domain.archive.ArchiveService
import hntech.hntechserver.domain.category.CategoryService
import hntech.hntechserver.domain.category.CreateCategoryForm
import hntech.hntechserver.file.File
import hntech.hntechserver.file.FileRepository
import hntech.hntechserver.file.FileService
import hntech.hntechserver.setMockSession
import hntech.hntechserver.testFile
import hntech.hntechserver.testFile2
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ArchiveControllerTest {

    @Autowired lateinit var mvc: MockMvc
    @Autowired lateinit var mapper: ObjectMapper
    @Autowired lateinit var fileService: FileService
    @Autowired lateinit var fileRepository: FileRepository
    @Autowired lateinit var categoryService: CategoryService
    @Autowired lateinit var archiveService: ArchiveService

    @BeforeEach
    fun `자료, 제품 카테고리 세팅`() {
        // 파일 세팅
        val testFileEntity1 = fileService.saveFile(testFile)
        fileService.saveFile(testFile)

        // 제품 카테고리 세팅
        categoryService.createCategory(CreateCategoryForm("스프링클러", type = "product", imageFileId = testFileEntity1.id))
        categoryService.createCategory(CreateCategoryForm("신축배관", type = "product", imageFileId = testFileEntity1.id))

        // 자료실 카테고리 세팅
        categoryService.createCategory(CreateCategoryForm("일반자료"))
        categoryService.createCategory(CreateCategoryForm("제품승인서"))
    }

    @AfterEach
    fun `mock 파일 삭제`() = fileService.deleteAllFiles(fileRepository.findAll())

    fun initDummyArchive(): Archive {
        val savedFileEntities: MutableList<File> =
            fileService.saveAllFiles(
                listOf<MultipartFile>(testFile, testFile, testFile)
            )
        val form = ArchiveForm(
            title = "스프링클러 자료 입니다",
            categoryName = "스프링클러",
            notice = "false",
            content = "테스트",
            files = savedFileEntities.map { it.serverFilename }.toList()
        )
        return archiveService.createArchive(form)
    }

    @Test
    fun `자료실 생성`() {
        val savedFileEntities: MutableList<File> =
            fileService.saveAllFiles(
                listOf<MultipartFile>(testFile, testFile, testFile)
            )
        val body = ArchiveForm(
            title = "스프링클러 자료 입니다",
            categoryName = "스프링클러",
            notice = "true",
            content = "테스트",
            files = savedFileEntities.map { it.serverFilename }.toList()
        )

        mvc.post("/archive") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(body)
            session = setMockSession()
        }
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("productCategoryName") { value("스프링클러") } }
            .andExpect { jsonPath("archiveCategoryName") { value("일반자료") } }
            .andExpect { jsonPath("new") { value("true") } }
            .andDo { print() }
    }

    @Test
    fun `자료 하나 조회`() {
        mvc.get("/archive/${initDummyArchive().id!!}") {}
            .andExpect { status { isOk() } }
            .andDo { print() }
    }

    @Test
    fun `자료 목록 조회`() {
        for (i: Int in 0 .. 20) initDummyArchive()

        mvc.get("/archive/all?page=0") {}
            .andExpect { status { isOk() } }
            .andDo { print() }
    }

    @Test
    fun `자료 수정 성공`() {
        val archive = initDummyArchive()

        val savedFileEntities: MutableList<File> =
            fileService.saveAllFiles(
                listOf<MultipartFile>(testFile2, testFile2, testFile2)
            )
        val body = ArchiveForm(
            title = "수정",
            categoryName = "신축배관",
            notice = "false",
            content = "수정본",
            files = savedFileEntities.map { it.serverFilename }.toList()
        )
        println(body)
        print(mapper.writeValueAsString(body))

        mvc.put("/archive/${archive.id!!}") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(body)
            session = setMockSession()
        }
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("id") { value(archive.id!!) } }
            .andExpect { jsonPath("productCategoryName") { value("신축배관") } }
            .andExpect { jsonPath("archiveCategoryName") { value("제품승인서") } }
            .andExpect { jsonPath("notice") { value("false") } }
            .andExpect { jsonPath("title") { value("수정") } }
            .andDo { print() }
    }

    @Test
    fun `자료 삭제`() {
        val archive = initDummyArchive()

        mvc.delete("/archive/${archive.id!!}") {
            session = setMockSession()
        }
            .andExpect { status { isOk() } }
            .andExpect { jsonPath("result") { value(true) } }
            .andDo { print() }
    }
}