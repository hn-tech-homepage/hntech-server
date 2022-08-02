//
//package hntech.hntechserver.category
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.http.MediaType
//import org.springframework.mock.web.MockMultipartFile
//import org.springframework.test.web.servlet.*
//import org.springframework.transaction.annotation.Transactional
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//class CategoryControllerTest {
//    @Autowired
//    lateinit var mvc: MockMvc
//
//    @Autowired
//    lateinit var mapper: ObjectMapper
//
//    @Autowired
//    lateinit var categoryService: CategoryService
//
//    @BeforeEach
//    fun mockDataSetUp() {
//        // 제품 카테고리 세팅
//        for (i: Int in 1..3) {
//            val img = MockMultipartFile("image", "test.jpg", "image/jpeg", "test".byteInputStream())
//            val form = ItemCategoryRequest(categoryName = "스프링클러$i", image = img)
//            categoryService.createItemCategory(form)
//        }
//
//        // 자료실 카테고리 세팅
//        for (i: Int in 4..6) {
//            val form = ArchiveCategoryRequest(categoryName = "일반자료$i")
//            categoryService.createArchiveCategory(form)
//        }
//
//    }
//
//    @Test
//    @DisplayName("[POST /item] 제품 카테고리 생성")
//    fun createItemCategory() {
//        val img = MockMultipartFile("image", "test.jpg", "image/jpeg", "test".byteInputStream())
//        mvc.multipart("/category/item") {
//            file(img)
//            params?.set("categoryName", "스프링클러")
//        }
//            .andDo { print() }
//            .andExpect { status { isOk() } }
//    }
//
//    @Test
//    @DisplayName("[POST /archive] 자료실 카테고리 생성")
//    fun createArchiveCategory() {
//        val body = ArchiveCategoryRequest("일반자료")
//        mvc.post("/category/archive") {
//            contentType = MediaType.APPLICATION_JSON
//            content = mapper.writeValueAsString(body)
//        }
//            .andDo { print() }
//            .andExpect { status { isOk() } }
//    }
//
//    @Test
//    @DisplayName("[GET /items] 제품 카테고리 전체 조회")
//    fun getAllItemCategories() {
//        mvc.get("/category/items") {}
//            .andDo { print() }
//            .andExpect { status { isOk() } }
//    }
//
//    @Test
//    @DisplayName("[GET /archives] 자료실 카테고리 전체 조회")
//    fun getAllArchiveCategories() {
//        mvc.get("/category/archives") {}
//            .andDo { print() }
//            .andExpect { status { isOk() } }
//    }
//
//    @Test
//    @DisplayName("[POST /item/{categoryId}] 제품 카테고리 수정")
//    fun updateItemCategory() {
//        val img = MockMultipartFile("image", "test.jpg", "image/jpeg", "test".byteInputStream())
//        val form = ItemCategoryRequest(categoryName = "스프링클러", image = img)
//        val category: Category = categoryService.createItemCategory(form)
//
//        val newImg = MockMultipartFile("image", "new.jpg", "image/jpeg", "test".byteInputStream())
//
//        mvc.multipart("/category/item/${category.id}") {
//            file(newImg)
//            params?.set("categoryName", "신축배관")
//        }
//            .andDo { print() }
//            .andExpect { status { isOk() } }
//    }
//
//    @Test
//    @DisplayName("[POST /archive/{categoryId}] 자료실 카테고리 수정")
//    fun updateArchiveCategory() {
//        val form = ArchiveCategoryRequest(categoryName = "일반자료")
//        val category: Category = categoryService.createArchiveCategory(form)
//
//        mvc.post("/category/archive/${category.id}") {
//            contentType = MediaType.APPLICATION_JSON
//            content = mapper.writeValueAsString(form)
//        }
//            .andDo { print() }
//            .andExpect { status { isOk() } }
//    }
//
//    @Test
//    @DisplayName("[DELETE /{categoryId}] 제품 카테고리 삭제")
//    fun deleteItemCategory() {
//        val img = MockMultipartFile("image", "test.jpg", "image/jpeg", "test".byteInputStream())
//        val form = ItemCategoryRequest(categoryName = "스프링클러", image = img)
//        val category: Category = categoryService.createItemCategory(form)
//
//        mvc.delete("/category/${category.id}") {}
//            .andDo { print() }
//            .andExpect { status { isOk() } }
//    }
//
//    @Test
//    @DisplayName("[DELETE /{categoryId}] 자료실 카테고리 삭제")
//    fun deleteArchiveCategory() {
//        val form = ArchiveCategoryRequest(categoryName = "일반자료")
//        val category: Category = categoryService.createArchiveCategory(form)
//
//        mvc.delete("/category/${category.id}") {}
//            .andDo { print() }
//            .andExpect { status { isOk() } }
//    }
//}