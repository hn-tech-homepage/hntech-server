package hntech.hntechserver.item

import hntech.hntechserver.category.Category
import hntech.hntechserver.file.ItemFile
import javax.persistence.*

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
    var files: MutableList<ItemFile> = mutableListOf(),

    @OneToMany(mappedBy = "imageItem", cascade = [CascadeType.ALL])
    var images: MutableList<ItemImage> = mutableListOf(),
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