package hntech.hntechserver.category

import hntech.hntechserver.CategoryException
import hntech.hntechserver.utils.logger
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.transaction.annotation.Transactional
import java.io.File

@SpringBootTest
@Transactional
class CategoryServiceTest {

    /**
     * 이거 이미지 저장하는 경로를 C드라이브에 dev 폴더로 해놔서 테스트 하고싶으면 거기다 dev 이름으로 폴더 만들고 돌려야 함
     */

    @Autowired
    lateinit var categoryRepository: CategoryRepository

    @Autowired
    lateinit var categoryService: CategoryService

    val log = logger()

    fun <T> logResult(actual: T, expected: T) {
        log.info("result\nactual \t\t: {} \nexpected \t: {}", actual.toString(), expected.toString())
    }

    @AfterEach
    fun deleteMockImage() {
        val names: List<String> = listOf("스프링클러", "스프링클러1", "스프링클러2", "스프링클러3", "신축배관")
        for (name: String in names) {
            val category = categoryRepository.findByCategoryName(name) ?: continue
            val testedFile = File(category.categoryImagePath)
            if (testedFile.exists()) testedFile.delete()
        }
    }

    @Test
    @DisplayName("제품 카테고리 생성 성공")
    fun createItemCategory() {
        // given
        val img = MockMultipartFile("image", "test.jpg", "image/jpeg", "test".byteInputStream())
        val form = ItemCategoryRequest(categoryName = "스프링클러", image = img)

        // when
        val expected = categoryService.createItemCategory(form)
        val actual = categoryRepository.findByCategoryName("스프링클러")!!

        // then
        assertThat(actual).isEqualTo(expected)
        logResult(actual, expected)
    }

    @Test
    @DisplayName("제품 카테고리 생성 실패 - 이미지 null")
    fun createFailImageNull() {
        val emptyFile = MockMultipartFile("null", null)
        val form = ItemCategoryRequest(categoryName = "스프링클러", image = emptyFile)

        assertThatThrownBy {
            categoryService.createItemCategory(form)
        }.isInstanceOf(CategoryException::class.java)
            .hasMessage("대표 이미지를 설정해야 합니다.")
    }

    @Test
    @DisplayName("자료실 카테고리 생성 성공")
    fun createArchiveCategory() {
        // given
        val form = ArchiveCategoryRequest(categoryName = "일반자료")

        // when
        val expected = categoryService.createArchiveCategory(form)
        val actual = categoryRepository.findByCategoryName("일반자료")!!

        // then
        assertThat(actual).isEqualTo(expected)
        logResult(actual, expected)
    }

    @Test
    @DisplayName("카테고리 생성 실패 - 중복 이름")
    fun createFailByName() {
        // given
        val form1 = ArchiveCategoryRequest(categoryName = "일반자료")
        val form2 = ArchiveCategoryRequest(categoryName = "일반자료")

        // when
        categoryService.createArchiveCategory(form1)

        // then
        assertThatThrownBy {
            categoryService.createArchiveCategory(form2)
        }.isInstanceOf(CategoryException::class.java)
    }

    @Test
    @DisplayName("제품 카테고리 전체 조회")
    fun getAllItemCategories() {
        // given
        val img = MockMultipartFile("image", "test.jpg", "image/jpeg", "test".byteInputStream())
        val temp = mutableListOf<Category>()
        for (i: Int in 1..3) {
            val form = ItemCategoryRequest(categoryName = "스프링클러$i", image = img)
            val itemCategory = categoryService.createItemCategory(form)
            temp.add(itemCategory)
        }

        // when
        val expected = categoryService.getAllItemCategories()

        // then
        assertThat(temp).isEqualTo(expected)
        logResult(temp, expected)
    }

    @Test
    @DisplayName("자료실 카테고리 전체 조회")
    fun getAllArchiveCategories() {
        // given
        val actual = mutableListOf<Category>()
        for (i: Int in 1..3) {
            val form = ArchiveCategoryRequest(categoryName = "일반자료$i")
            val archiveCategory = categoryService.createArchiveCategory(form)
            actual.add(archiveCategory)
        }

        // when
        val expected = categoryService.getAllArchiveCategories()

        // then
        assertThat(actual).isEqualTo(expected)
        logResult(actual, expected)
    }

    @Test
    @DisplayName("제품 카테고리 수정 성공")
    fun updateItemCategory() {
        // given
        val img = MockMultipartFile("image", "old.jpg", "image/jpeg", "test".byteInputStream())
        val form = ItemCategoryRequest(categoryName = "스프링클러", image = img)
        val category: Category = categoryService.createItemCategory(form)

        val newImg = MockMultipartFile("image", "new.jpg", "image/jpeg", "test".byteInputStream())
        val updateForm = ItemCategoryRequest(categoryName = "신축배관", image = newImg)

        // when
        val expected: List<Category> = categoryService.updateItemCategory(category.id!!, updateForm)
        val actual: Category = categoryRepository.findByCategoryName("신축배관")!!

        // then
        assertThat(expected).contains(actual)
        logResult(
            actual = convertItemDto(actual),
            expected = ItemCategoryListResponse(expected.map { convertItemDto(it) })
        )
    }

    @Test
    @DisplayName("제품 카테고리 수정 성공 - 대표 이미지 변경X")
    fun updateItemCategoryNoImage() {
        // given
        val img = MockMultipartFile("image", "old.jpg", "image/jpeg", "test".byteInputStream())
        val form = ItemCategoryRequest(categoryName = "스프링클러", image = img)
        val category: Category = categoryService.createItemCategory(form)

        val updateForm = ItemCategoryRequest(categoryName = "신축배관", image = MockMultipartFile("null", null))

        // when
        val expected: List<Category> = categoryService.updateItemCategory(category.id!!, updateForm)
        val actual: Category = categoryRepository.findByCategoryName("신축배관")!!

        // then
        assertThat(expected).contains(actual)
        assertThat(actual.categoryImagePath).isEqualTo(category.categoryImagePath)
        log.info("\npath before = {}, after = {}", category.categoryImagePath, actual.categoryImagePath)

    }

    @Test
    @DisplayName("자료실 카테고리 수정 성공")
    fun updateArchiveCategory() {
        // given
        val form = ArchiveCategoryRequest(categoryName = "일반자료")
        val category: Category = categoryService.createArchiveCategory(form)

        val updateForm = ArchiveCategoryRequest(categoryName = "동영상자료")

        // when
        val expected: List<Category> = categoryService.updateArchiveCategory(category.id!!, updateForm)
        val actual: Category = categoryRepository.findByCategoryName("동영상자료")!!

        // then
        assertThat(expected).contains(actual)
        logResult(
            actual = convertItemDto(actual),
            expected = ItemCategoryListResponse(expected.map { convertItemDto(it) })
        )
    }

    @Test
    @DisplayName("카테고리 삭제")
    fun deleteCategory() {
        // given
        val img = MockMultipartFile("image", "old.jpg", "image/jpeg", "test".byteInputStream())
        val form1 = ItemCategoryRequest(categoryName = "스프링클러", image = img)
        val itemCategory: Category = categoryService.createItemCategory(form1)

        val form2 = ArchiveCategoryRequest(categoryName = "일반자료")
        val archiveCategory: Category = categoryService.createArchiveCategory(form2)

        // when
        val expected1: Boolean = categoryService.deleteCategory(itemCategory.id!!)
        val actual1: Boolean = !categoryRepository.existsById(itemCategory.id!!)

        val expected2: Boolean = categoryService.deleteCategory(archiveCategory.id!!)
        val actual2: Boolean = !categoryRepository.existsById(archiveCategory.id!!)

        // then
        assertThat(expected1).isEqualTo(actual1)
        logResult(actual1, expected1)
        assertThat(expected2).isEqualTo(actual2)
        logResult(actual2, expected2)
    }


}