package hntech.hntechserver.product

import hntech.hntechserver.category.CategoryService
import hntech.hntechserver.file.FileService
import hntech.hntechserver.utils.config.PRODUCT_IMAGE
import hntech.hntechserver.utils.config.REPRESENTATIVE_IMAGE
import hntech.hntechserver.utils.config.STANDARD_IMAGE
import hntech.hntechserver.utils.logging.logger
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

    private fun setFileTypes(files: UploadedFiles, product: Product) {
        fileService.getFile(files.representativeImage).update(
            fileProduct = product,
            type = REPRESENTATIVE_IMAGE
        )
        files.productImages.forEach {
            fileService.getFile(it).update(
                fileProduct = product,
                type = PRODUCT_IMAGE
            )
        }
        files.standardImages.forEach {
            fileService.getFile(it).update(
                fileProduct = product,
                type = STANDARD_IMAGE
            )
        }
        files.docFiles?.forEach {
            fileService.getFile(it.fileId).update(
                fileProduct = product,
                type = it.filename
            )
        }
    }

    // 마지막 순서의 제품 조회
    @Transactional(readOnly = true)
    private fun getLastProduct(): Product? = productRepository.findFirstByOrderBySequenceDesc()

    // 제품 생성
    fun createProduct(form: ProductCreateForm): Product {
        checkProductName(form.productName)

        // 카테고리 가져오기
        val category = categoryService.getCategory(form.categoryName)

        productRepository.adjustSequenceToRightAll()

        // 제품 글 저장
        val product = productRepository.save(
            Product(
                productCategory = category,
                productName = form.productName,
                description = form.description
            )
        )
        
        // 제품 파일 저장
        setFileTypes(form.files, product)
        product.update(files = fileService.getFiles(form.files.getFileIds()))

        return product
    }

    /**
     * 카테고리 이름?
     * 있으면 해당 카테고리의 제품 리스트를 반환, 없을 경우 전체 제품 리스트
     */
    @Transactional(readOnly = true)
    fun getAllProducts(categoryName: String? = null): List<Product> {
        categoryName?.let {
            return categoryService.getCategory(it).products.sortedBy { p -> p.sequence }
        } ?: run {
            return productRepository.findAll(Sort.by(Sort.Direction.ASC, "sequence"))
        }
    }

    @Transactional(readOnly = true)
    fun getProduct(id: Long): Product =
        productRepository.findById(id).orElseThrow { throw ProductException(PRODUCT_NOT_FOUND) }

    // 제품 수정
    fun updateProduct(id: Long, form: ProductUpdateForm): Product {
        val product = getProduct(id)
        
        // 수정하려는 이름이 현재 이름과 같지 않으면 이름 중복 체크
        if (product.productName != form.productName) checkProductName(product.productName)

        fileService.deleteAllFiles(product.files)
        setFileTypes(form.files, product)

        product.update(
            productName = form.productName,
            description = form.description,
            files = fileService.getFiles(form.files.getFileIds())
        )

        return product
    }

    // 제품 순서 변경
    fun updateProductSequence(productId: Long, targetProductId: Long): List<Product> {
        val currentSequence: Int = getProduct(productId).sequence
        var targetSequence: Int = when(targetProductId) {
            0L -> getLastProduct()!!.sequence + 1
            else -> getProduct(targetProductId).sequence
        }

        if (currentSequence > targetSequence)
            productRepository.adjustSequenceToRight(targetSequence, currentSequence)
        else
            productRepository.adjustSequenceToLeft(currentSequence, targetSequence)

        if (targetProductId == 0L || currentSequence < targetSequence)
            targetSequence -= 1

        getProduct(productId).update(sequence = targetSequence)

        return getAllProducts()
    }

    fun deleteProduct(id: Long) {
        val product = getProduct(id)
        fileService.deleteAllFiles(product.files)
        productRepository.adjustSequenceToLeftAll(product.sequence)
        productRepository.delete(product)
    }
}