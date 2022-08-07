package hntech.hntechserver.file

import hntech.hntechserver.archive.Archive
import hntech.hntechserver.product.Product
import javax.persistence.*

@Entity
class File(
    @Id @GeneratedValue
    @Column(name = "file_id")
    val id: Long? = null,

    var originalFilename: String = "",
    var serverFilename: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id")
    var fileArchive: Archive? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var fileProduct: Product? = null,

    /**
     * 파일 타입 지정용 프로퍼티
     * 제품의 경우 제품 사진, 규격 사진을 구분
     * 자료실의 경우 이미지, 영상, 문서 파일을 구분
     */
    var type: String = ""
) {
    fun setFileType(type: String) { this.type = type }
    fun update(
        originalFilename: String? = null,
        serverFilename: String? = null,
        fileArchive: Archive? = null,
        fileProduct: Product? = null
    ) {
        originalFilename?.let { this.originalFilename = it }
        serverFilename?.let { this.serverFilename = it }
        fileArchive?.let { this.fileArchive = it }
        fileProduct?.let { this.fileProduct = it }
    }
}
