package hntech.hntechserver.domain.category

import hntech.hntechserver.domain.archive.Archive
import hntech.hntechserver.domain.file.File
import hntech.hntechserver.domain.product.Product
import javax.persistence.*

@Entity
class Category (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    val id: Long? = null,

    var categoryName: String = "",
    var type: String = "", // archive, product

    // product 카테고리 전용
    var sequence: Int = 1,
    var showInMain: String = "false",

    var role: String = "", // parent, child

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: Category? = null,

    @OneToMany(mappedBy = "parent", cascade = [CascadeType.ALL], orphanRemoval = true)
    var children: MutableList<Category> = mutableListOf(),

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "file_id")
    var file: File? = null, // 제품 한정 카테고리 대표 이미지 경로 저장

    @OneToMany(mappedBy = "productCategory", cascade = [CascadeType.ALL])
    var products: MutableList<Product> = mutableListOf(),

    // archive 카테고리 전용 (모든 카테고리가 들어감)
    @OneToMany(mappedBy = "category", cascade = [CascadeType.ALL])
    var archives: MutableList<Archive> = mutableListOf(),
) {
    fun update(
        categoryName: String? = null,
        showInMain: String? = null,
        sequence: Int? = null,
        file: File? = null
    ) {
        categoryName?.let { this.categoryName = it }
        showInMain?.let { this.showInMain = it }
        sequence?.let { this.sequence = it }
        file?.let { this.file = it }
    }

    fun addChild(category: Category) {
        this.children.add(category);
    }

    fun mappingParent(category: Category) {
        this.parent = category
    }
}