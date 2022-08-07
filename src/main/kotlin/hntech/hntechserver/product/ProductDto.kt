package hntech.hntechserver.product

import hntech.hntechserver.file.FileResponse
import hntech.hntechserver.file.convertDto
import javax.validation.constraints.NotBlank

// 문서 파일(버튼 이름 포함)
data class DocFiles(
    var filename: String,
    var fileId: Long
)

// 미리 업로드된 파일 요청용 폼
data class UploadedFiles(
    // 대표 사진, 제품 사진, 규격 사진 서버에 저장된 파일 id로 요청
    var representativeImage: Long,
    var productImages: List<Long>,
    var standardImages: List<Long>,
    
    var docFiles: List<DocFiles>
) {
    fun getFileIds(): List<Long> {
        val result: MutableList<Long> = mutableListOf()
        result.add(this.representativeImage)
        this.productImages.forEach { result.add(it) }
        this.standardImages.forEach { result.add(it) }
        this.docFiles.forEach { result.add(it.fileId) }
        return result
    }
}

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

data class ProductDetailResponse(
    var id: Long?,
    var category: String,
    var productName: String,
    var description: String,
    var files: List<FileResponse>
) {
    constructor(product: Product): this(
        id = product.id,
        category = product.productCategory.categoryName,
        productName = product.productName,
        description = product.description,
        files = product.files.map { convertDto(it) }
    )
}

data class ProductSimpleResponse(
    var id: Long?,
    var sequence: Int,
    var productName: String,
    var image: FileResponse
) {
    constructor(product: Product): this(
        id = product.id,
        sequence = product.sequence,
        productName = product.productName,
        image = convertDto(product.files[0])
    )
}

data class ProductListResponse(
    var products: List<ProductSimpleResponse>
)