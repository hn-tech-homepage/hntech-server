package hntech.hntechserver.domain.product

import hntech.hntechserver.domain.category.Category
import hntech.hntechserver.domain.file.File
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
        productCategory: Category? = null,
        productName: String? = null,
        description: String? = null,
        files: MutableList<File>? = null,
        sequence: Int? = null
    ) {
        productCategory?.let { this.productCategory = it }
        productName?.let { this.productName = it }
        description?.let { this.description = it }
        files?.let { this.files = it }
        sequence?.let { this.sequence = it }
    }
    fun addFile(file: File) = this.files.add(file)
}