package hntech.hntechserver.product

import hntech.hntechserver.category.CategoryService
import hntech.hntechserver.file.File
import hntech.hntechserver.file.FileService
import hntech.hntechserver.utils.config.PRODUCT_IMAGE
import hntech.hntechserver.utils.config.REPRESENTATIVE_IMAGE
import hntech.hntechserver.utils.config.STANDARD_IMAGE
import hntech.hntechserver.utils.logger
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryService: CategoryService,
    private val fileService: FileService
) {
    val log = logger()

    // 제품명 중복체크
    @Transactional(readOnly = true)
    private fun checkProductName(name: String) {
        if (productRepository.existsByProductName(name)) throw ProductException(DUPLICATE_PRODUCT_NAME)
    }

    private fun setFileTypes(files: UploadedFiles) {
        fileService.getFile(files.representativeImage).setFileType(REPRESENTATIVE_IMAGE)
        files.productImages.forEach { fileService.getFile(it).setFileType(PRODUCT_IMAGE) }
        files.standardImages.forEach { fileService.getFile(it).setFileType(STANDARD_IMAGE) }
        files.docFiles.forEach { fileService.getFile(it.fileId).setFileType(it.filename) }
    }

    // 마지막 순서의 제품 조회
    @Transactional(readOnly = true)
    private fun getLastProduct(): Product? = productRepository.findFirstByOrderBySequenceDesc()

    fun createProduct(form: ProductCreateForm): Product {
        checkProductName(form.productName)

        // 카테고리 가져오기
        val category = categoryService.getCategory(form.categoryName)

        // 저장된 파일들의 type 지정
        setFileTypes(form.files)

        return productRepository.save(
            Product(
                productCategory = category,
                productName = form.productName,
                description = form.description,
                sequence = getLastProduct()?.let { it.sequence + 1 } ?: run { 1 },
                files = fileService.getFiles(form.files.getFileIds())
            )
        )
    }

    /**
     * 카테고리 이름?
     * 있으면 해당 카테고리의 제품 리스트를 반환, 없을 경우 전체 제품 리스트
     */
    @Transactional(readOnly = true)
    fun getAllProducts(categoryName: String?): List<Product> {
        categoryName?.let {
            return categoryService.getCategory(it).products
        } ?: run {
            return productRepository.findAll(Sort.by(Sort.Direction.DESC, ""))
        }
    }

    @Transactional(readOnly = true)
    fun getProduct(id: Long): Product =
        productRepository.findById(id).orElseThrow { throw ProductException(PRODUCT_NOT_FOUND) }

    fun updateProduct(id: Long, form: ProductUpdateForm): Product {
        val product = getProduct(id)
        
        // 수정하려는 이름이 현재 이름과 같지 않으면 이름 중복 체크
        if (product.productName != form.productName) checkProductName(product.productName)

        fileService.deleteAllFiles(product.files)
        setFileTypes(form.files)

        product.update(
            productName = form.productName,
            description = form.description,
            files = fileService.getFiles(form.files.getFileIds())
        )

        return product
    }

    fun deleteProduct(id: Long) {
        val product = getProduct(id)
        fileService.deleteAllFiles(product.files)
        productRepository.delete(product)
    }
}