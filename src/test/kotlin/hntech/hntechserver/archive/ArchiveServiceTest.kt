package hntech.hntechserver.archive

import com.querydsl.jpa.impl.JPAQueryFactory
import hntech.hntechserver.domain.archive.*
import hntech.hntechserver.domain.archive.QArchive.archive
import hntech.hntechserver.domain.category.CategoryService
import hntech.hntechserver.domain.category.CreateCategoryForm
import hntech.hntechserver.domain.file.File
import hntech.hntechserver.domain.file.FileRepository
import hntech.hntechserver.domain.file.FileService
import hntech.hntechserver.testFile
import hntech.hntechserver.testFile2
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@SpringBootTest
@Transactional
class ArchiveServiceTest {

    @Autowired lateinit var archiveService: ArchiveService
    @Autowired lateinit var archiveRepository: ArchiveRepository
    @Autowired lateinit var categoryService: CategoryService
    @Autowired lateinit var fileService: FileService
    @Autowired lateinit var fileRepository: FileRepository
    @Autowired lateinit var query: JPAQueryFactory

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
    fun `로컬에 저장되었던 mock 파일들을 삭제한다`() =
        fileService.deleteAllFiles(fileRepository.findAll())


    // 자료 하나 저장
    fun initDummyArchive(): Archive {
        val productCategory = categoryService.getCategory("스프링클러")
        val archiveCategory = categoryService.getCategory("일반자료")
        val savedFileEntities: MutableList<File> =
            fileService.saveAllFiles(
                listOf<MultipartFile>(testFile, testFile, testFile)
            )
        val form = ArchiveForm(
            title = "스프링클러 자료 입니다",
            categoryName = productCategory.categoryName,
            notice = "false",
            content = "테스트",
            files = savedFileEntities.map { it.serverFilename }.toList()
        )
        return archiveService.createArchive(form)
    }

    @Test
    fun `자료실 글 생성 성공`() {
        // when
        val expected: Archive = initDummyArchive()
        val actual: Archive = archiveRepository.findAll()[0]

        // then
        expected shouldBe actual
        expected.category!!.categoryName shouldBe "스프링클러"
        println("createTime = ${expected.createTime}, updatedTime = ${expected.updateTime}")
    }

    @Test
    fun `자료실 하나 조회`() {
        // given
        val archive = initDummyArchive()

        // when
        val expected: Archive = archiveService.getArchive(archive.id!!)
        val actual: Archive = archive

        // then
        expected shouldBe actual
    }

    @Test
    fun `자료 목록 조회(페이징)`() {
        // given
        for (i: Int in 0 .. 20) initDummyArchive()
        val pageable: Pageable = PageRequest.of(0, 15, Sort.Direction.DESC, "id")

        // when
//        val expected: Page<Archive> = archiveService.getArchives(pageable)
//
//        // then
//        expected.totalPages shouldBe 2
//        expected.totalElements shouldBe 21
    }
    
    @Test
    fun `자료 검색(페이징)`() {
        /**
         * 카테고리, 제목, 내용
         * 페이징
         */
        var pageable: Pageable = PageRequest.of(0, 15, Sort.Direction.DESC, "id")
        var categoryName: String? = null
        var keyword: String? = null

        val result = query
            .selectFrom(archive)
            .where(
                keywordContains(keyword)
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        println(result.map { ArchiveSimpleResponse(it).id })
    }

    fun keywordContains(keyword: String?) =
        keyword?.let { archive.title.contains(it).or(archive.content.contains(it)) }

    @Test
    fun `자료 수정 성공`() {
        // given
        val oldArchive = initDummyArchive()
        val productCategory = categoryService.getCategory("신축배관")
        val archiveCategory = categoryService.getCategory("제품승인서")
        val newSavedFileEntities: MutableList<File> =
            fileService.saveAllFiles(
                listOf<MultipartFile>(testFile2, testFile2, testFile2)
            )
        val newArchiveForm = ArchiveForm(
            title = "스프링클러 자료 입니다2",
            categoryName = productCategory.categoryName,
            notice = "true",
            content = "테스트2",
            files = newSavedFileEntities.map { it.serverFilename }.toList()
        )

        // when
        val expected: Archive = archiveService.updateArchive(oldArchive.id!!, newArchiveForm)
        val actual: Archive = archiveService.getArchive(oldArchive.id!!)

        // then
        expected shouldBe actual
        expected.files shouldBe newSavedFileEntities
        expected.category!!.categoryName shouldBe "신축배관"
        expected.notice shouldBe "true"
        expected.title shouldBe "스프링클러 자료 입니다2"
        expected.content shouldBe "테스트2"
    }

    @Test
    fun `자료 삭제 성공`() {
        // given
        val archive = initDummyArchive()

        // when
        val expected = archiveService.deleteArchive(archive.id!!)

        // then
        expected shouldBe true
        shouldThrow<ArchiveException> {
            archiveService.getArchive(archive.id!!)
        }
    }
}