package hntech.hntechserver.file

import hntech.hntechserver.archive.Archive
import hntech.hntechserver.item.Item
import javax.persistence.*

@Entity
class File(
    @Id @GeneratedValue
    @Column(name = "file_id")
    var id: Long? = null,

    var originFileName: String,
    var serverFileName: String,
    var savedPath: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id")
    var fileArchive: Archive? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    var fileItem: Item? = null,
)