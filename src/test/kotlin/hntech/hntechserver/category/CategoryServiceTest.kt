package hntech.hntechserver.category

import hntech.hntechserver.TestUtil.Companion.deleteFiles
import hntech.hntechserver.TestUtil.Companion.initTestCategories
import hntech.hntechserver.TestUtil.Companion.initTestFile
import hntech.hntechserver.TestUtil.Companion.log
import hntech.hntechserver.TestUtil.Companion.logResult
import hntech.hntechserver.file.FileRepository
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
    private val fileRepository: FileRepository
): FunSpec() {
    
    // 테스트용 데이터 가져오기
    private val testFile = initTestFile()
    private val testCategories = initTestCategories()

    init {
        beforeTest { categoryRepository.deleteAll(); fileRepository.deleteAll() }

        // 모든 테스트 종료 후 로컬 스토리지의 모든 파일 삭제
        afterContainer { deleteFiles() }

        test("카테고리 생성 성공") {
            val form = CategoryCreateForm("카테고리", testFile)

            val expected = convertDto(categoryService.createCategory(form))
            val actual = categoryRepository.findAll().map { convertDto(it) }

            actual shouldContain expected
            logResult(actual, expected)
        }
        test("카테고리 생성 실패: 이미 존재하는 카테고리 이름") {
            val form = CategoryCreateForm(testCategories[0].categoryName, testFile)
            categoryService.createCategory(form)

            shouldThrow<CategoryException> { categoryService.createCategory(form) }
        }
        test("모든 카테고리 조회") {
            testCategories.forEach { categoryService.createCategory(it) }
            val expected = convertDto(categoryService.getAllCategories()).categories
            val actual = convertDto(categoryRepository.findAll()).categories

            actual shouldContainAll expected
            logResult(actual, expected)
        }
        test("카테고리 ID로 조회") {
            val savedCategories: MutableList<Category> = mutableListOf()
            testCategories.forEach { savedCategories.add(categoryService.createCategory(it)) }
            val id = 0 // 0~4

            val expected = convertDto(categoryService.getCategory(savedCategories[id].id!!))
            val actual = convertDto(categoryRepository.findById(savedCategories[id].id!!).get())

            actual shouldBe expected
        }
        test("카테고리 이름으로 조회") {
            val name = "카테고리2"
            testCategories.forEach { categoryService.createCategory(it) }
            val expected = convertDto(categoryService.getCategory(name))
            val actual = convertDto(categoryRepository.findByCategoryName(name) ?: Category())

            actual shouldBe expected
        }
        test("카테고리 수정") {
            val origin = categoryService.createCategory(CategoryCreateForm("카테고리", testFile))
            val updateForm = CategoryUpdateForm("수정된 카테고리", testFile, true)

            categoryService.updateCategory(origin.id!!, updateForm)

            val actual = categoryRepository.findById(origin.id!!).get()

            actual.categoryName shouldBe updateForm.categoryName
            actual.showInMain shouldBe updateForm.showInMain
        }
        test("카테고리 수정 후 카테고리 조회") {
            val savedCategories: MutableList<Category> = mutableListOf()
            testCategories.forEach { savedCategories.add(categoryService.createCategory(it)) }

            categoryService.updateCategory(
                savedCategories[0].id!!, CategoryUpdateForm(savedCategories[0].categoryName, testFile, true))
            categoryService.updateCategory(
                savedCategories[2].id!!, CategoryUpdateForm(savedCategories[2].categoryName, testFile, true))
            categoryService.updateCategory(
                savedCategories[4].id!!, CategoryUpdateForm(savedCategories[4].categoryName, testFile, true))

            val actual = categoryService.getMainCategories()
            val actual2 = categoryService.getAllCategories()

            actual.forAll { it.showInMain shouldBe true }
            actual2[0].showInMain shouldBe true
            actual2[1].showInMain shouldBe false
            actual2[2].showInMain shouldBe true
            actual2[3].showInMain shouldBe false
            actual2[4].showInMain shouldBe true
        }
        test("카테고리 순서 변경(맨 뒤 -> 맨 앞)") {
            val savedCategories: MutableList<Category> = mutableListOf()
            testCategories.forEach { savedCategories.add(categoryService.createCategory(it)) }

            categoryService.updateCategorySequence(
                categoryId = savedCategories[savedCategories.size - 1].id!!,
                targetCategoryId = savedCategories[0].id!!
            )
            categoryRepository.findAllByOrderBySequence()[0].categoryName shouldBe "카테고리5"
        }
        test("카테고리 순서 변경(맨 앞 -> 맨 뒤)") {
            val savedCategories: MutableList<Category> = mutableListOf()
            testCategories.forEach { savedCategories.add(categoryService.createCategory(it)) }

            categoryService.updateCategorySequence(
                categoryId = savedCategories[0].id!!,
                targetCategoryId = 0L
            )
            categoryRepository.findAllByOrderBySequence()[4].categoryName shouldBe "카테고리1"
        }
        test("카테고리 순서 변경(맨 앞 -> 4번째)") {
            val savedCategories: MutableList<Category> = mutableListOf()
            testCategories.forEach { savedCategories.add(categoryService.createCategory(it)) }

            categoryService.updateCategorySequence(
                categoryId = savedCategories[0].id!!,
                targetCategoryId = savedCategories[3].id!!
            )
            categoryRepository.findAllByOrderBySequence()[3].categoryName shouldBe "카테고리1"
        }
        test("카테고리 순서 변경(맨 뒤 -> 3번째)") {
            val savedCategories: MutableList<Category> = mutableListOf()
            testCategories.forEach { savedCategories.add(categoryService.createCategory(it)) }

            categoryService.updateCategorySequence(
                categoryId = savedCategories[savedCategories.size - 1].id!!,
                targetCategoryId = savedCategories[2].id!!
            )
            categoryRepository.findAllByOrderBySequence()[2].categoryName shouldBe "카테고리5"
        }
        test("카테고리 삭제") {
            val category = categoryService.createCategory(CategoryCreateForm("카테고리", testFile))

            categoryService.deleteCategory(category.id!!)

            shouldThrow<CategoryException> {
                categoryService.getCategory(category.id!!)
            }
        }
        test("카테고리 삭제 실패: 없는 카테고리") {

        }
    }
}