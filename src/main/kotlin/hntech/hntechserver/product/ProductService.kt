package hntech.hntechserver.product

import hntech.hntechserver.category.CategoryService
import hntech.hntechserver.file.FileService
import hntech.hntechserver.utils.error.DUPLICATE_PRODUCT_NAME
import hntech.hntechserver.utils.error.PRODUCT_NOT_FOUND
import hntech.hntechserver.utils.error.ProductException
import hntech.hntechserver.utils.logger
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryService: CategoryService,
    private val fileService: FileService
) {
    val log = logger()

    // 제품명 중복체크
    fun checkProductName(name: String) {
        if (productRepository.existsByProductName(name)) throw ProductException(DUPLICATE_PRODUCT_NAME)
    }

    fun createProduct(form: ProductCreateForm): Product {
        checkProductName(form.productName)

        // 카테고리 가져오기
        val category = categoryService.getCategory(form.categoryName)

        // 제품 글 저장
        val product = productRepository.save(convertEntity(form, category))

        // 제품 파일 저장
        product.updateFiles(fileService.saveAllFiles(form.files, product))

        return product
    }

    /**
     * 카테고리 이름?
     * 있으면 해당 카테고리의 제품 리스트를 반환, 없을 경우 전체 제품 리스트
     */
    fun getAllProducts(categoryName: String?): List<Product> {
        categoryName?.let {
            return categoryService.getCategory(it).products
        } ?: run {
            return productRepository.findAll()
        }
    }

    fun getProduct(id: Long): Product =
        productRepository.findById(id).orElseThrow { throw ProductException(PRODUCT_NOT_FOUND) }

    fun updateProduct(id: Long, form: ProductUpdateForm): Product {
        val product = getProduct(id)
        product.update(
            productName = form.productName,
            description = form.description,
            files = fileService.saveAllFiles(form.files, product)
        )
        return product
    }

    fun deleteProduct(id: Long) {
        val product = getProduct(id)
        fileService.deleteAllFiles(product.files)
        productRepository.delete(product)
    }
}