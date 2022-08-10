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

    var notice: String = "false",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_category_id")
    var archiveCategory: Category? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_category_id")
    var productCategory: Category? = null,

    @OneToMany(mappedBy = "fileArchive", cascade = [CascadeType.ALL])
    var files: MutableList<File> = mutableListOf(),

    // 중복되는 부분
    var title: String = "",
    var content: String = "",
) : BaseTimeEntity() {
    fun update(
        notice: String? = null,
        title: String? = null,
        content: String? = null,
        archiveCategory: Category? = null,
        productCategory: Category? = null,
        files: MutableList<File>? = null
    ) {
        notice?.let { this.notice = notice }
        title?.let { this.title = title }
        content?.let { this.content = content }
        archiveCategory?.let { this.archiveCategory = archiveCategory }
        productCategory?.let { this.productCategory = productCategory }
        files?.let { this.files = files }
    }
}