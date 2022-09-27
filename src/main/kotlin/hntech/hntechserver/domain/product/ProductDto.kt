package hntech.hntechserver.domain.product

import hntech.hntechserver.common.PRODUCT_IMAGE
import hntech.hntechserver.common.REPRESENTATIVE_IMAGE
import hntech.hntechserver.common.STANDARD_IMAGE
import hntech.hntechserver.domain.file.FileDetailResponse
import hntech.hntechserver.domain.file.FileResponse
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.NotBlank

data class ProductRequestForm(
    @field:NotBlank
    var categoryName: String,

    @field:NotBlank
    var productName: String,
    var description: String,

    var representativeImage: MultipartFile? = null,
    var productImages: List<MultipartFile>?,
    var standardImages: List<MultipartFile>?,
    var docFiles: List<MultipartFile>?
)

data class ProductDocFileForm(
    var filename: String
)

data class UpdateProductSequenceForm(
    var currentProductId: Long,
    var targetProductId: Long,
)

data class ProductSimpleResponse(
    var id: Long?,
    var productName: String,
    var image: FileResponse? = null
) {
    constructor(product: Product): this(
        id = product.id,
        productName = product.productName,
        image = product.files
            .filter { it.type == REPRESENTATIVE_IMAGE }
            .getOrNull(0)?.let { FileResponse(it) }
    )
}

data class ProductDetailResponse(
    var id: Long?,
    var category: String,
    var productName: String,
    var description: String,
    var files: ProductFilesResponse
) {
    constructor(product: Product): this(
        id = product.id,
        category = product.productCategory.categoryName,
        productName = product.productName,
        description = product.description,
        files = getUploadedFiles(product)
    )
}

data class ProductFilesResponse(
    var representativeImage: FileResponse? = null,
    var productImages: MutableList<FileResponse> = mutableListOf(),
    var standardImages: MutableList<FileResponse> = mutableListOf(),
    var docFiles: MutableList<FileDetailResponse> = mutableListOf()
)
// Product 에 저장된 파일들을 type 으로 분류
fun getUploadedFiles(product: Product): ProductFilesResponse {
    val result = ProductFilesResponse()
    product.files.forEach {
        when(it.type) {
            REPRESENTATIVE_IMAGE -> result.representativeImage = FileResponse(it)
            PRODUCT_IMAGE -> result.productImages.add(FileResponse(it))
            STANDARD_IMAGE -> result.standardImages.add(FileResponse(it))
            else -> result.docFiles.add(FileDetailResponse(it))
        }
    }
    return result
}

data class ProductListResponse(
    var products: List<ProductSimpleResponse>
)