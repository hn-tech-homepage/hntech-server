package hntech.hntechserver.file

import hntech.hntechserver.archive.Archive
import hntech.hntechserver.product.Product
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
class File(
    @Id @GeneratedValue
    @Column(name = "file_id")
    val id: Long? = null,

    var originFileName: String = "",
    var serverFileName: String = "",
)

@Entity
@DiscriminatorValue(value = "archive")
class ArchiveFile(
    originFileName: String,
    serverFileName: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id")
    var fileArchive: Archive? = null
) : File(originFileName = originFileName, serverFileName = serverFileName)

@Entity
@DiscriminatorValue(value = "product")
class ProductFile(
    originFileName: String,
    serverFileName: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var fileProduct: Product? = null,
): File(originFileName = originFileName, serverFileName = serverFileName)