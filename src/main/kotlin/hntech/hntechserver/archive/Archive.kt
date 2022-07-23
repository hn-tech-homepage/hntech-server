package hntech.hntechserver.archive

import hntech.hntechserver.category.Category
import hntech.hntechserver.file.File
import hntech.hntechserver.utils.BaseTimeEntity
import javax.persistence.*

@Entity
class Archive(
    @Id @GeneratedValue
    @Column(name = "archive_id")
    var id: Long? = null,

    var isNotice: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var archiveCategory: Category? = null,

    @OneToMany(mappedBy = "fileArchive", cascade = [CascadeType.ALL])
    var files: MutableList<File> = mutableListOf(),

    // 중복되는 부분
    var title: String = "",
    var content: String = "",
) : BaseTimeEntity()