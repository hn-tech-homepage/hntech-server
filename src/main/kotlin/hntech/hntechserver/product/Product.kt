package hntech.hntechserver.product

import hntech.hntechserver.category.Category
import hntech.hntechserver.file.File
import javax.persistence.*

@Entity
class Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var productCategory: Category,

    var productName: String = "",
    var description: String = "",

    @OneToMany(mappedBy = "fileProduct", cascade = [CascadeType.ALL])
    var files: MutableList<File> = mutableListOf(),

    var sequence: Int = 1
) {
    fun update(
        id: Long? = null,
        productCategory: Category? = null,
        productName: String? = null,
        description: String? = null,
        files: MutableList<File>? = null,
        sequence: Int? = null
    ) {
        id?.let { this.id = it }
        productCategory?.let { this.productCategory = it }
        productName?.let { this.productName = it }
        description?.let { this.description = it }
        files?.let { this.files = it }
        sequence?.let { this.sequence = it }
    }
    fun addFile(file: File) = this.files.add(file)
}