package hntech.hntechserver.product

import hntech.hntechserver.category.Category
import hntech.hntechserver.file.ProductFile
import javax.persistence.*

@Entity
class Product(
    @Id @GeneratedValue
    @Column(name = "product_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    var productCategory: Category,

    var productName: String = "",
    var description: String = "",

    @OneToMany(mappedBy = "fileProduct", cascade = [CascadeType.ALL])
    var files: MutableList<ProductFile> = mutableListOf()
) {
    fun update(productName: String, description: String, files: MutableList<ProductFile>) {
        this.productName = productName
        this.description = description
        this.files = files
    }
    fun updateFiles(files: MutableList<ProductFile>) { this.files = files }
}