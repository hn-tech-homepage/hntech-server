package hntech.hntechserver.domain.archive

import hntech.hntechserver.domain.category.Category
import hntech.hntechserver.domain.file.File
import hntech.hntechserver.common.BaseTimeEntity
import javax.persistence.*

@Entity
class Archive(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_id")
    var id: Long? = null,

    var notice: String = "false",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: Category? = null,

    @OneToMany(mappedBy = "fileArchive", cascade = [CascadeType.ALL], orphanRemoval = true)
    var files: MutableList<File> = mutableListOf(),

    var title: String = "",

    @Column(length = 750)
    var content: String = "",
) : BaseTimeEntity() {
    fun update(
        notice: String? = null,
        title: String? = null,
        content: String? = null,
        category: Category? = null,
        files: MutableList<File>? = null,
    ) {
        notice?.let { this.notice = notice }
        title?.let { this.title = title }
        content?.let { this.content = content }
        category?.let { this.category = category }
        files?.let { this.files = files }
    }
}