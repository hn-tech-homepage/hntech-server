package hntech.hntechserver.category

import hntech.hntechserver.archive.Archive
import hntech.hntechserver.item.Item
import javax.persistence.*

@Entity
class Category (
    @Id @GeneratedValue
    @Column(name = "category_id")
    var id: Long? = null,

    var categoryName: String = "",
    var sequence: Int = 0,
    var type: String = "", // 자료실, 제품, 자료

    @OneToMany(mappedBy = "archiveCategory", cascade = [CascadeType.ALL])
    var archives: MutableList<Archive> = mutableListOf(),

    @OneToMany(mappedBy = "itemCategory", cascade = [CascadeType.ALL])
    var items: MutableList<Item> = mutableListOf(),
)