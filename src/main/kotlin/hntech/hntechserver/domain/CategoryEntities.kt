package hntech.hntechserver.domain

import javax.persistence.*

@Entity
class Category (
    @Id @GeneratedValue
    @Column(name = "category_id")
    var id: Long? = null,

    var categoryName: String = "",
    var order: Int = 0,
    var type: String = "", // 자료실, 제품, 자료

    @OneToMany(mappedBy = "archiveCategory", cascade = [CascadeType.ALL])
    var archives: MutableSet<Archive> = HashSet(),

    @OneToMany(mappedBy = "itemCategory", cascade = [CascadeType.ALL])
    var items: MutableSet<Item> = HashSet(),
)

@Entity
class Archive(
    @Id @GeneratedValue
    @Column(name = "archive_id")
    var id: Long? = null,

    var isNotice: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var archiveCategory: Category,

    @OneToMany(mappedBy = "fileArchive", cascade = [CascadeType.ALL])
    var files: MutableSet<File> = HashSet(),

    // 중복되는 부분
    var title: String = "",
    var content: String = "",
    var viewCount: Int = 0,
) : BaseTimeEntity()

@Entity
class File(
    @Id @GeneratedValue
    @Column(name = "file_id")
    var id: Long? = null,

    var fileName: String = "",
    var src: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archive_id")
    var fileArchive: Archive,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    var fileItem: Item,
)

@Entity
class Item(
    @Id @GeneratedValue
    @Column(name = "item_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var itemCategory: Category,

    var itemName: String = "",
    var description: String = "",

    @OneToMany(mappedBy = "fileItem", cascade = [CascadeType.ALL])
    var files: MutableSet<File> = HashSet(),

    @OneToMany(mappedBy = "imageItem", cascade = [CascadeType.ALL])
    var images: MutableSet<ItemImage> = HashSet(),
)

@Entity
class ItemImage(
    @Id @GeneratedValue
    @Column(name = "item_image_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    var imageItem: Item,

    var src: String = "",
    var type: String = "", // 그림, 스펙
)