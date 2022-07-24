package hntech.hntechserver

import hntech.hntechserver.category.*
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
        val names: List<String> = listOf("스프링클러", "스프링클러1", "스프링클러2", "스프링클러3")
        for (name: String in names) {
            val category = categoryRepository.findByCategoryName(name) ?: continue
            val testedFile = File(category.categoryImagePath)
            if (testedFile.exists()) testedFile.delete()
        }
    }

    @Test
    @DisplayName("제품 카테고리 생성")
    fun createItemCategory() {
        // given
        val img = MockMultipartFile("image", "test.jpg", "image/jpeg", "test".byteInputStream())
        val form = CreateItemCategoryRequest(categoryName = "스프링클러", image = img)

        // when
        val expected = categoryService.createItemCategory(form)
        val actual = convertItemDto(categoryRepository.findByCategoryName("스프링클러")!!)

        // then
        assertThat(actual).isEqualTo(expected)
        logResult(actual, expected)
    }

    @Test
    @DisplayName("자료실 카테고리 생성")
    fun createArchiveCategory() {
        // given
        val form = CreateArchiveCategoryRequest(categoryName = "일반자료")

        // when
        val expected = categoryService.createArchiveCategory(form)
        val actual = convertArchiveDto(categoryRepository.findByCategoryName("일반자료")!!)

        // then
        assertThat(actual).isEqualTo(expected)
        logResult(actual, expected)
    }

    @Test
    @DisplayName("카테고리 생성 실패 - 중복 이름")
    fun duplicateCategoryName() {
        // given
        val form1 = CreateArchiveCategoryRequest(categoryName = "일반자료")
        val form2 = CreateArchiveCategoryRequest(categoryName = "일반자료")

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
        val temp = mutableListOf<ItemCategoryResponse>()
        for (i: Int in 1..3) {
            val form = CreateItemCategoryRequest(categoryName = "스프링클러$i", image = img)
            val itemCategory = categoryService.createItemCategory(form)
            temp.add(itemCategory)
        }

        // when
        val expected = categoryService.getAllItemCategories()
        val actual = ItemCategoryListResponse(temp)

        // then
        assertThat(actual).isEqualTo(expected)
        logResult(actual, expected)
    }

    @Test
    @DisplayName("자료실 카테고리 전체 조회")
    fun getAllArchiveCategories() {
        // given
        val temp = mutableListOf<ArchiveCategoryResponse>()
        for (i: Int in 1..3) {
            val form = CreateArchiveCategoryRequest(categoryName = "일반자료$i")
            val itemCategory = categoryService.createArchiveCategory(form)
            temp.add(itemCategory)
        }

        // when
        val expected = categoryService.getAllArchiveCategories()
        val actual = ArchiveCategoryListResponse(temp)

        // then
        assertThat(actual).isEqualTo(expected)
        logResult(actual, expected)
    }


}