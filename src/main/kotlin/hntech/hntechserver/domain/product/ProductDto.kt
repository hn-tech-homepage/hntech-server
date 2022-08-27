package hntech.hntechserver.domain.product

import hntech.hntechserver.config.PRODUCT_IMAGE
import hntech.hntechserver.config.REPRESENTATIVE_IMAGE
import hntech.hntechserver.config.STANDARD_IMAGE
import hntech.hntechserver.domain.file.FileDetailResponse
import hntech.hntechserver.domain.file.FileResponse
import javax.validation.constraints.NotBlank

// 문서 파일(버튼 이름 포함)
data class DocFile(
    var filename: String,
    var fileId: Long
)

// 미리 업로드된 파일 요청용 폼
data class UploadedFiles(
    // 대표 사진, 제품 사진, 규격 사진 서버에 저장된 파일 id로 요청
    var representativeImage: Long,
    var productImages: List<Long>,
    var standardImages: List<Long>,
    
    var docFiles: List<DocFile>?
) {
    fun getFileIds(): List<Long> {
        val result: MutableList<Long> = mutableListOf()
        result.add(this.representativeImage)
        this.productImages.forEach { result.add(it) }
        this.standardImages.forEach { result.add(it) }
        this.docFiles?.forEach { result.add(it.fileId) }
        return result
    }
}

data class UploadedFilesResponse(
    var representativeImage: FileResponse? = null,
    var productImages: MutableList<FileResponse> = mutableListOf(),
    var standardImages: MutableList<FileResponse> = mutableListOf(),
    var docFiles: MutableList<FileDetailResponse> = mutableListOf()
)

data class ProductCreateForm(
    @field:NotBlank
    var categoryName: String,

    @field:NotBlank
    var productName: String,
    var description: String,

    var files: UploadedFiles
)

data class ProductUpdateForm(
    @field:NotBlank
    var productName: String,

    var description: String,
    var files: UploadedFiles
)

data class ProductSimpleResponse(
    var id: Long?,
    var productName: String,
    var image: FileResponse
) {
    constructor(product: Product): this(
        id = product.id,
        productName = product.productName,
        image = FileResponse(product.files[0])
    )
}

data class ProductDetailResponse(
    var id: Long?,
    var category: String,
    var productName: String,
    var description: String,
    var files: UploadedFilesResponse
) {
    constructor(product: Product): this(
        id = product.id,
        category = product.productCategory.categoryName,
        productName = product.productName,
        description = product.description,
        files = getUploadedFiles(product)
    )
}

data class ProductListResponse(
    var products: List<ProductSimpleResponse>
)

// Product 에 저장된 파일들을 type 으로 분류한 객체 
fun getUploadedFiles(product: Product): UploadedFilesResponse {
    val result = UploadedFilesResponse()
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