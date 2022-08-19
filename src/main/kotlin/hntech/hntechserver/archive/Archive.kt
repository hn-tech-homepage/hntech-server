package hntech.hntechserver.archive

import hntech.hntechserver.category.Category
import hntech.hntechserver.file.File
import hntech.hntechserver.utils.BaseTimeEntity
import javax.persistence.*

@Entity
@SequenceGenerator(
    name = "ARCHIVE_PK_GENERATOR",
    sequenceName = "ARCHIVE_SEQ",
    initialValue = 1,
    allocationSize = 50
)
class Archive(
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ARCHIVE_PK_GENERATOR")
    @Column(name = "archive_id")
    var id: Long? = null,

    var notice: String = "false",

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "archive_category_id")
//    var archiveCategory: Category? = null,
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "product_category_id")
//    var productCategory: Category? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var category: Category? = null,

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
//        archiveCategory: Category? = null,
//        productCategory: Category? = null,
        category: Category? = null,
        files: MutableList<File>? = null
    ) {
        notice?.let { this.notice = notice }
        title?.let { this.title = title }
        content?.let { this.content = content }
//        archiveCategory?.let { this.archiveCategory = archiveCategory }
//        productCategory?.let { this.productCategory = productCategory }
        category?.let { this.category = category }
        files?.let { this.files = files }
    }
}