//package hntech.hntechserver.category
//
//import hntech.hntechserver.TestUtil.Companion.deleteFiles
//import hntech.hntechserver.TestUtil.Companion.initTestCategories
//import hntech.hntechserver.TestUtil.Companion.initTestFile
//import hntech.hntechserver.TestUtil.Companion.logResult
//import io.kotest.assertions.throwables.shouldThrow
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.matchers.collections.shouldContain
//
//import org.springframework.boot.test.context.SpringBootTest
//
//@SpringBootTest
//internal class CategoryServiceTest(
//    private val categoryService: CategoryService,
//    private val categoryRepository: CategoryRepository
//): FunSpec() {
//
//    // 테스트용 데이터 가져오기
//    private val testFile = initTestFile()
//    private val testCategories = initTestCategories()
//
//    init {
//        // 테스트 시작 전 한 번 실행
//        beforeContainer {
//            testCategories.forEach { categoryService.createCategory(it) }
//        }
//
//        // 모든 테스트 종료 후 로컬 스토리지의 모든 파일 삭제
//        afterContainer { deleteFiles() }
//
//        context("카테고리 생성") {
//            test("성공") {
//                val form = CategoryCreateForm("카테고리", testFile)
//
//                val expected = convertDto(categoryService.createCategory(form))
//                val actual = categoryRepository.findAll().map { convertDto(it) }
//
//                actual shouldContain expected
//                logResult(actual, expected)
//            }
//            test("실패: 이미 존재하는 카테고리 이름") {
//                val form = CategoryCreateForm(testCategories[0].categoryName, testFile)
//
//                shouldThrow<CategoryException> { categoryService.createCategory(form) }
//            }
//        }
//        context("카테고리 조회") {
//            test("모든 카테고리 조회") {
//
//            }
//            test("메인 카테고리 조회") {
//
//            }
//        }
//    }
//}