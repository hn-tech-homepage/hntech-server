package hntech.hntechserver.file

import hntech.hntechserver.archive.Archive
import hntech.hntechserver.archive.ArchiveFile
import hntech.hntechserver.product.Product
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)
class File(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    val id: Long? = null,

    var originalFilename: String = "",
    var serverFilename: String = "",
    var savedPath: String = "",

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "archive_id")
//    var fileArchive: Archive? = null,

    @OneToMany(mappedBy = "file", cascade = [CascadeType.ALL])
    var archives: MutableList<ArchiveFile> = mutableListOf(),

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
    fun update(
        originalFilename: String? = null,
        serverFilename: String? = null,
        fileArchive: Archive? = null,
        fileProduct: Product? = null,
        type: String? = null,
        savedPath: String? = null,
    ) {
        originalFilename?.let { this.originalFilename = it }
        serverFilename?.let { this.serverFilename = it }
//        fileArchive?.let { this.fileArchive = it }
        fileProduct?.let { this.fileProduct = it }
        type?.let { this.type = it }
        savedPath?.let { this.savedPath = it }
    }

    // DB 파일 엔티티가 삭제되면 로컬에 저장되있는 실제 파일도 삭제
    @PreRemove
    fun deleteSavedRealFile() {
        java.io.File(savedPath).delete()
    }
    
}
