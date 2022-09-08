package hntech.hntechserver.domain.product

import hntech.hntechserver.common.*
import hntech.hntechserver.config.FILE_TYPE_PRODUCT
import hntech.hntechserver.domain.category.CategoryService
import hntech.hntechserver.domain.file.FileService
import hntech.hntechserver.utils.logging.logger
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository,
    private val categoryService: CategoryService,
    private val fileService: FileService
) {
    val log = logger()

    // 제품명 중복체크
    private fun checkProductName(name: String) {
        if (productRepository.existsByProductName(name))
            throw ProductException(DUPLICATE_PRODUCT_NAME)
    }

    // 마지막 순서의 제품 조회
    private fun getLastProduct(): Product? = productRepository.findFirstByOrderBySequenceDesc()

    private fun saveAndAddProductFile(product: Product, file: MultipartFile, type: String) =
        product.addFile(fileService.saveFile(file, saveType = FILE_TYPE_PRODUCT)
            .update(fileProduct = product, type = type))

    private fun saveProductFiles(product: Product, form: ProductRequestForm) {
        form.representativeImage?.let {
            if (!it.isEmpty) saveAndAddProductFile(product, it, REPRESENTATIVE_IMAGE)
        }
        form.productImages?.forEach { saveAndAddProductFile(product, it, PRODUCT_IMAGE) }
        form.standardImages?.forEach { saveAndAddProductFile(product, it, STANDARD_IMAGE) }
        form.docFiles?.forEach { saveAndAddProductFile(product, it, DOC_FILE) }
    }

    /**
     * 제품 생성
     */
    fun createProduct(form: ProductRequestForm): ProductDetailResponse {
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
        saveProductFiles(product, form)

        return ProductDetailResponse(product)
    }

    /**
     * 카테고리 이름?
     * 있으면 해당 카테고리의 제품 리스트를 반환, 없을 경우 전체 제품 리스트
     */
    @Transactional(readOnly = true)
    fun getAllProducts(categoryName: String? = null): ProductListResponse {
        val products: List<Product> = categoryName?.let {
            categoryService.getCategory(it).products.sortedBy { p -> p.sequence }
        } ?: run {
            productRepository.findAll(Sort.by(Sort.Direction.ASC, "sequence"))
        }
        return ProductListResponse(
            products.map { ProductSimpleResponse(it) }
        )
    }

    @Transactional(readOnly = true)
    fun getProduct(id: Long): Product =
        productRepository.findById(id).orElseThrow { throw ProductException(PRODUCT_NOT_FOUND) }

    @Transactional(readOnly = true)
    fun getProductToDto(id: Long) = ProductDetailResponse(getProduct(id))

    // 제품 문서 파일 이름 변경
    fun updateProductDocFile(productId: Long, fileId: Long, form: ProductDocFileForm): Product {
        fileService.getFile(fileId).update(type = form.filename)
        return getProduct(productId)
    }

    /**
     * 제품 수정
     */
    fun updateProduct(id: Long, form: ProductRequestForm): ProductDetailResponse {
        val product = getProduct(id)

        // 수정하려는 이름이 현재 이름과 같지 않으면 이름 중복 체크
        if (product.productName != form.productName) checkProductName(form.productName)

        product.update(
            productCategory = categoryService.getCategory(form.categoryName),
            productName = form.productName,
            description = form.description
        )

        // 제품 파일 저장
        saveProductFiles(product, form)

        return ProductDetailResponse(product)
    }

    // 제품 순서 변경
    fun updateProductSequence(productId: Long, targetProductId: Long): ProductListResponse {
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

    fun deleteProduct(id: Long): BoolResponse {
        productRepository.deleteById(id)
        return BoolResponse(true)
    }

    fun deleteAttachedFile(productId: Long, fileId: Long): BoolResponse {
        val product = getProduct(productId)
        product.files
            .find { it.id == fileId }
            .let { product.files.remove(it) }
        return BoolResponse(true)
    }
}