package hntech.hntechserver.archive

import hntech.hntechserver.category.Category
import hntech.hntechserver.file.File
import hntech.hntechserver.utils.BaseTimeEntity
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

//    @OneToMany(mappedBy = "fileArchive", cascade = [CascadeType.ALL])
//    var files: MutableList<File> = mutableListOf(),

    @OneToMany(mappedBy = "archive", cascade = [CascadeType.ALL])
    var files: MutableList<ArchiveFile> = mutableListOf(),

    var title: String = "",

    @Column(length = 750)
    var content: String = "",


) : BaseTimeEntity() {
    fun update(
        notice: String? = null,
        title: String? = null,
        content: String? = null,
        category: Category? = null,
        files: MutableList<ArchiveFile>? = null
    ) {
        notice?.let { this.notice = notice }
        title?.let { this.title = title }
        content?.let { this.content = content }
        category?.let { this.category = category }
        files?.let { this.files = files }
    }
}

@Entity
class ArchiveFile(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_file_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id")
    var archive: Archive,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    var file: File,
)