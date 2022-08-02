package hntech.hntechserver.category

import hntech.hntechserver.archive.Archive
import hntech.hntechserver.file.File
import hntech.hntechserver.product.Product
import javax.persistence.*

@Entity
class Category (
    @Id @GeneratedValue
    @Column(name = "category_id")
    val id: Long? = null,

    var categoryName: String = "",

    @OneToOne
    @JoinColumn(name = "category_file_id")
    var categoryFile: File? = null, // 제품 한정 카테고리 대표 이미지 경로 저장

    @OneToMany(mappedBy = "archiveCategory", cascade = [CascadeType.ALL])
    var archives: MutableList<Archive> = mutableListOf(),

    @OneToMany(mappedBy = "productCategory", cascade = [CascadeType.ALL])
    var products: MutableList<Product> = mutableListOf(),
) {
    // 카테고리 수정
    fun update(newName: String) { this.categoryName = newName }
    fun update(newName: String, newFile: File) {
        this.categoryName = newName
        this.categoryFile = newFile
    }
}