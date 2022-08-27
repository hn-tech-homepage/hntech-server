package hntech.hntechserver.domain.file

import hntech.hntechserver.domain.admin.Admin
import hntech.hntechserver.domain.archive.Archive
import hntech.hntechserver.domain.product.Product
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id")
    var fileArchive: Archive? = null,

//    @OneToMany(mappedBy = "file", cascade = [CascadeType.ALL])
//    var archives: MutableList<ArchiveFile> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var fileProduct: Product? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    var fileAdmin: Admin? = null,

    /**
     * 제품 파일 타입 지정용 프로퍼티
     * 제품 사진, 규격 사진, 문서 파일 등을 구분
     */
    var type: String = ""
) {
    fun update(
        originalFilename: String? = null,
        serverFilename: String? = null,
        fileArchive: Archive? = null,
        fileProduct: Product? = null,
        fileAdmin: Admin? = null,
        type: String? = null,
        savedPath: String? = null,
    ): File {
        originalFilename?.let { this.originalFilename = it }
        serverFilename?.let { this.serverFilename = it }
        fileArchive?.let { this.fileArchive = it }
        fileProduct?.let { this.fileProduct = it }
        fileAdmin?.let { this.fileAdmin = it }
        type?.let { this.type = it }
        savedPath?.let { this.savedPath = it }
        return this
    }

    // DB 파일 엔티티가 삭제되면 로컬에 저장되있는 실제 파일도 삭제
    @PreRemove
    fun deleteSavedRealFile() {
        java.io.File(savedPath).delete()
    }
    
}
