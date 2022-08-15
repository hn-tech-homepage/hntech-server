package hntech.hntechserver.category

import hntech.hntechserver.file.File
import hntech.hntechserver.file.FileRepository
import hntech.hntechserver.file.FileService
import hntech.hntechserver.initTestFile
import hntech.hntechserver.utils.logging.logger
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
internal class CategoryServiceTest(
    private val categoryService: CategoryService,
    private val categoryRepository: CategoryRepository,
    private val fileService: FileService,
    private val fileRepository: FileRepository
): FunSpec() {
    val log = logger()
    // 테스트용 데이터 가져오기
    private val testFile = initTestFile()

    var savedCategories: MutableList<Category> = mutableListOf()
    var savedFiles: MutableList<File> = mutableListOf()
    var cLastIdx: Int = 0
    var fLastIdx: Int = 0

    fun uploadFile(): File {
        val saved = fileService.saveFile(testFile);
        savedFiles = fileRepository.findAll()
        fLastIdx = savedFiles.size - 1
        return saved
    }
    fun deleteFile() {
        fileService.deleteFile(savedFiles[fLastIdx].serverFilename)
        savedFiles = fileRepository.findAll()
        fLastIdx = savedFiles.size - 1
    }

    init {
        // 테스트 시작 전 테스트 데이터 저장
        beforeSpec {
            repeat(10) {
                categoryService.createCategory(CreateCategoryForm("카테고리$it", uploadFile().id))
            }
        }

        beforeTest {
            savedCategories = categoryRepository.findAllByOrderBySequence() as MutableList<Category>
            savedFiles = fileRepository.findAll()
            cLastIdx = savedCategories.size - 1
            fLastIdx = savedFiles.size - 1
            log.info("savedCategories: {}", savedCategories.size)
        }

        test("카테고리 생성 성공") {
            val form = CreateCategoryForm("생성된 카테고리", uploadFile().id)

            val expected = convertDto(categoryService.createCategory(form))
            val actual = categoryRepository.findAllByOrderBySequence().map { convertDto(it) }

            actual shouldContain expected
        }
        test("카테고리 생성 실패: 이미 존재하는 카테고리 이름") {
            val form = CreateCategoryForm(savedCategories[0].categoryName, uploadFile().id)

            shouldThrow<CategoryException> { categoryService.createCategory(form) }
            deleteFile()
        }
        test("모든 카테고리 조회") {
            val expected = convertDto(categoryService.getAllCategories()).categories
            val actual = convertDto(categoryRepository.findAll()).categories

            actual shouldContainAll expected
        }
        test("카테고리 ID로 조회") {
            val idx = 0 // 0~4

            val expected = convertDto(categoryService.getCategory(savedCategories[idx].id!!))
            val actual = convertDto(categoryRepository.findById(savedCategories[idx].id!!).get())

            actual shouldBe expected
        }
        test("카테고리 이름으로 조회") {
            val name = savedCategories[0].categoryName

            val expected = convertDto(categoryService.getCategory(name))
            val actual = convertDto(categoryRepository.findByCategoryName(name) ?: Category())

            actual shouldBe expected
        }
        test("카테고리 수정") {
            val updateForm = UpdateCategoryForm("수정된 카테고리", savedFiles[fLastIdx].id, true)

            categoryService.updateCategory(savedCategories[cLastIdx].id!!, updateForm)

            val actual = categoryRepository.findById(savedCategories[cLastIdx].id!!).get()

            actual.categoryName shouldBe updateForm.categoryName
            actual.showInMain shouldBe updateForm.showInMain
        }
        test("카테고리 수정 후 카테고리 조회") {
            categoryService.updateCategory(
                savedCategories[0].id!!, UpdateCategoryForm(savedCategories[0].categoryName, uploadFile().id, true))
            categoryService.updateCategory(
                savedCategories[2].id!!, UpdateCategoryForm(savedCategories[2].categoryName, uploadFile().id, true))
            categoryService.updateCategory(
                savedCategories[4].id!!, UpdateCategoryForm(savedCategories[4].categoryName, uploadFile().id, true))

            val actual = categoryService.getMainCategories() // 메인 카테고리 조회

            actual.forAll { it.showInMain shouldBe true }
        }
        test("카테고리 수정 실패: 메인 카테고리 수 초과") {
            shouldThrow<CategoryException> {
                savedCategories.forEach {
                    categoryService.updateCategory(
                        it.id!!,
                        UpdateCategoryForm(
                            categoryName = it.categoryName,
                            imageFileId = uploadFile().id,
                            showInMain = true
                        )
                    )
                }
            }
        }
        test("카테고리 순서 변경(맨 뒤 -> 맨 앞)") {
            val form = UpdateCategorySequenceForm(
                savedCategories[cLastIdx].id!!,
                savedCategories[0].id!!
            )
            categoryService.updateCategorySequence(form)

            val expected = savedCategories[cLastIdx].categoryName
            val actual = categoryRepository.findAllByOrderBySequence()[0].categoryName

            actual shouldBe expected
        }
        test("카테고리 순서 변경(맨 앞 -> 맨 뒤)") {
            val form = UpdateCategorySequenceForm(
                savedCategories[0].id!!,
                0L
            )
            categoryService.updateCategorySequence(form)

            val expected = savedCategories[0].categoryName
            val actual = categoryRepository.findAllByOrderBySequence()[cLastIdx].categoryName

            actual shouldBe expected
        }
        test("카테고리 순서 변경(맨 앞 -> 4번째)") {
            val form = UpdateCategorySequenceForm(
                savedCategories[0].id!!,
                savedCategories[3].id!!
            )
            categoryService.updateCategorySequence(form)

            val expected = savedCategories[0].categoryName
            val actual = categoryRepository.findAllByOrderBySequence()[2].categoryName

            actual shouldBe expected
        }
        test("카테고리 순서 변경(맨 뒤 -> 3번째)") {
            val form = UpdateCategorySequenceForm(
                savedCategories[cLastIdx].id!!,
                savedCategories[2].id!!
            )
            categoryService.updateCategorySequence(form)

            val expected = savedCategories[cLastIdx].categoryName
            val actual = categoryRepository.findAllByOrderBySequence()[2].categoryName

            actual shouldBe expected
        }
        test("카테고리 삭제") {
            uploadFile()
            val category = categoryService.createCategory(
                CreateCategoryForm("카테고리", savedFiles[fLastIdx].id))

            categoryService.deleteCategory(category.id!!)

            shouldThrow<CategoryException> {
                categoryService.getCategory(category.id!!)
            }
        }
        test("카테고리 삭제 실패: 없는 카테고리") {
            val id = 0L

            shouldThrow<CategoryException> {
                categoryService.deleteCategory(id)
            }
        }
    }
}
