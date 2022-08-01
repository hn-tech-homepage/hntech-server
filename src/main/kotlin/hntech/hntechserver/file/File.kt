package hntech.hntechserver.file

import hntech.hntechserver.archive.Archive
import hntech.hntechserver.product.Product
import javax.persistence.*

@Entity
@Inheritance
class File(
    @Id @GeneratedValue
    @Column(name = "file_id")
    val id: Long? = null,

    var originFileName: String = "",
    var serverFileName: String = "",
    var savedPath: String = "",
)

@Entity
class ArchiveFile(
    override var originFileName: String = "",
    override var serverFileName: String = "",
    override var savedPath: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id")
    var fileArchive: Archive? = null
) : File()

@Entity
class ProductFile(
    override var originFileName: String,
    override var serverFileName: String,
    override var savedPath: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var fileProduct: Product? = null,
): File()