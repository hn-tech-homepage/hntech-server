package hntech.hntechserver.category

import hntech.hntechserver.archive.Archive
import hntech.hntechserver.product.Product
import javax.persistence.*

@Entity
class Category (
    @Id @GeneratedValue
    @Column(name = "category_id")
    val id: Long? = null,

    var categoryName: String = "",
    var categoryImagePath: String = "", // 제품 한정 카테고리 대표 이미지 경로 저장

    @OneToMany(mappedBy = "archiveCategory", cascade = [CascadeType.ALL])
    var archives: MutableList<Archive> = mutableListOf(),

    @OneToMany(mappedBy = "productCategory", cascade = [CascadeType.ALL])
    var products: MutableList<Product> = mutableListOf(),
) {
    // 카테고리 수정
    fun update(newName: String) { this.categoryName = newName }
    fun update(newName: String, newPath: String) {
        this.categoryName = newName
        this.categoryImagePath = newPath
    }
}