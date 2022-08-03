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
)
