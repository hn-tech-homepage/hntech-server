package hntech.hntechserver.product

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.util.StringUtils
import com.querydsl.jpa.impl.JPAQueryFactory
import hntech.hntechserver.category.Category
import hntech.hntechserver.category.CategoryService
import hntech.hntechserver.category.CreateCategoryForm
import hntech.hntechserver.file.File
import hntech.hntechserver.file.FileService
import hntech.hntechserver.initTestFile
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.transaction.annotation.Transactional
import hntech.hntechserver.product.QProduct.product as product

@SpringBootTest
@Transactional
internal class ProductServiceTest {
    @Autowired lateinit var productService: ProductService
    @Autowired lateinit var productRepository: ProductRepository
    @Autowired lateinit var categoryService: CategoryService
    @Autowired lateinit var fileService: FileService

    @Autowired lateinit var query: JPAQueryFactory

    private val testFile = initTestFile()
    private val usedFiles: MutableList<File> = mutableListOf()

    fun uploadFile(): File {
        val file = fileService.saveFile(testFile)
        usedFiles.add(file)
        return file
    }

    companion object {
        @BeforeAll @JvmStatic
        fun initCategories(@Autowired categoryService: CategoryService) {
            categoryService.createCategory(CreateCategoryForm("카테고리1"))
            categoryService.createCategory(CreateCategoryForm("카테고리2"))
        }
    }
    @AfterEach
    fun clearProducts() {
        productRepository.deleteAll()
        fileService.deleteAllFiles(usedFiles)
        usedFiles.clear()
    }

    fun getCategoryList(): List<Category> = categoryService.getAllCategories()

    fun generateCreateForm(categoryIdx: Int, name: String, docNames: List<String>): ProductCreateForm {
        return ProductCreateForm(
            categoryName = getCategoryList()[categoryIdx].categoryName,
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

    @Test
    fun `제품 생성`() {
        // given
        val form = generateCreateForm(0, "제품", listOf("설명서", "인증서"))

        // when
        val createdProduct = productService.createProduct(form)

        // then
        productRepository.findById(createdProduct.id!!).get() shouldBe createdProduct
    }

    @Test
    fun `제품 검색 Querydsl 사용`() {
        // 검색 요건 : 카테고리 제품명 설명 모두 포함
        var page = 0;

        var category = getCategoryList()[0].id
        var name: String? = null
        var description: String? = null

        val pageable = PageRequest.of(page, 15, Sort.Direction.ASC, "sequence")
        repeat(16) { productService.createProduct(generateCreateForm(0, "제품$it", listOf())) }
        repeat(16) { productService.createProduct(generateCreateForm(1, "제품" + (it + 16), listOf())) }

        val result = query
            .selectFrom(product)
            .where(
                nameContains(name)?.or(descriptionContains(description))
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
        println(result.map { ProductSimpleResponse(it).id })
    }

    fun nameContains(name: String?) = name?.let { product.productName.contains(it) }
    fun descriptionContains(description: String?) = description?.let { product.description.contains(description) }

    @Test
    fun `제품 생성 실패 #이미 존재하는 제품명`() {
        // given
        productService.createProduct(generateCreateForm(0, "제품", listOf()))
        val form = generateCreateForm(0, productRepository.findAll()[0].productName, listOf("설명서"))

        // then
        shouldThrow<ProductException> { productService.createProduct(form) }
    }

    @Test
    fun `제품 목록 조회`() {
        // given
        repeat(3) { productService.createProduct(generateCreateForm(0, "제품$it", listOf())) }
        repeat(3) { productService.createProduct(generateCreateForm(1, "제품" + (it + 3), listOf())) }

        // when
        val allProducts = // 모든 제품 목록
            productService.getAllProducts()

        val firstCategoryProducts = // 첫 번째 카테고리의 제품 목록
            productService.getAllProducts(getCategoryList()[0].categoryName)
        println(firstCategoryProducts.map { it.sequence }.toString())

        val secondCategoryProducts = // 두 번째 카테고리의 제품 목록
            productService.getAllProducts(getCategoryList()[1].categoryName)
        println(secondCategoryProducts.map { it.sequence }.toString())

        // then
        allProducts shouldContainAll productRepository.findAll()
        firstCategoryProducts shouldContainAll getCategoryList()[0].products
        secondCategoryProducts shouldContainAll getCategoryList()[1].products
    }

    @Test
    fun `제품 상세 조회`() {
        // given
        val createdProduct = productService.createProduct(generateCreateForm(0, "제품", listOf()))

        // when
        val expected = productService.getProduct(createdProduct.id!!)

        // then
        createdProduct shouldBe expected
    }
    
    @Test
    fun `제품 상세 조회 실패 #없는 제품`() {
        shouldThrow<ProductException> { productService.getProduct(0L) }
    }

    @Test
    fun `제품 수정`() {
        // given
        val productId = productService.createProduct(generateCreateForm(0, "제품", listOf())).id!!
        val form = generateUpdateForm("제품", listOf())

        // when
        val expected = productService.updateProduct(productId, form)

        // then
        productRepository.findById(productId).get() shouldBe expected
    }

    @Test
    fun `제품 수정 실패 #없는 제품`() {
        shouldThrow<ProductException> {
            productService.updateProduct(0L, generateUpdateForm("제품", listOf()))
        }
    }
    
    @Test
    fun `제품 수정 실패 #이미 존재하는 제품명`() {
        // given
        repeat(2) { productService.createProduct(generateCreateForm(0, "제품$it", listOf())) }
        val form = generateUpdateForm("제품1", listOf())

        // then
        shouldThrow<ProductException> {
            productService.updateProduct(productService.getAllProducts()[0].id!!, form)
        }
    }

    @Test
    fun `제품 순서 변경`() {
        // given
        repeat(10) { // 테스트 제품 10개 저장
            productService.createProduct(generateCreateForm(0, "제품${9 - it}", listOf())) }

        // case1: 맨 앞 제품을 맨 뒤로
        var productList = productService.getAllProducts()
        productService.updateProductSequence(productList[0].id!!, 0L)
        productService.getAllProducts()[productList.size - 1].productName shouldBe "제품0"
        println(productService.getAllProducts().map { it.productName }.toString())
        
        // case2: 맨 뒤 제품을 맨 앞으로
        productList = productService.getAllProducts()
        productService.updateProductSequence(productList[productList.size - 1].id!!, productList[0].id!!)
        productService.getAllProducts()[0].productName shouldBe "제품0"
        println(productService.getAllProducts().map { it.productName }.toString())
        
        // case3: 맨 앞 제품을 네 번째 제품 앞으로
        productList = productService.getAllProducts()
        productService.updateProductSequence(productList[0].id!!, productList[3].id!!)
        productService.getAllProducts()[2].productName shouldBe "제품0"
        println(productService.getAllProducts().map { it.productName }.toString())

        // case3: 맨 뒤 제품을 세 번째 제품 앞으로
        productList = productService.getAllProducts()
        productService.updateProductSequence(productList[productList.size - 1].id!!, productList[2].id!!)
        productService.getAllProducts()[2].productName shouldBe "제품9"
        println(productService.getAllProducts().map { it.productName }.toString())
    }

    @Test
    fun `제품 삭제`() {
        // given
        val productId = productService.createProduct(generateCreateForm(0, "제품", listOf())).id!!

        // when
        productService.deleteProduct(productId)

        // then
        shouldThrow<ProductException> { productService.getProduct(productId) }
    }

    @Test
    fun `제품 삭제 실패 #없는 제품`() {
        shouldThrow<ProductException> { productService.deleteProduct(0L) }
    }
}