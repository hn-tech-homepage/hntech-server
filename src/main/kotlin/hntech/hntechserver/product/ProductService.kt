package hntech.hntechserver.product

import hntech.hntechserver.category.CategoryService
import hntech.hntechserver.file.FileService
import hntech.hntechserver.utils.logger
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryService: CategoryService,
    private val fileService: FileService
) {
    val log = logger()

    fun createProduct(form: ProductCreateForm): Product {
        // 카테고리 가져오기
        val category = categoryService.getCategory(form.categoryName)

        // 제품 글 저장
        val product = productRepository.save(convertEntity(form, category))

        // 제품 파일 저장
        product.updateFiles(fileService.saveAllFiles(form.files, product))

        return product
    }

    fun getAllProducts(): List<Product> = productRepository.findAll()

    fun getProduct(id: Long): Product
    = productRepository.findById(id).orElseThrow { throw NoSuchElementException("해당 제품을 찾을 수 없습니다.") }

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
        fileService.deleteAllFiles(product)
        productRepository.delete(product)
    }
}