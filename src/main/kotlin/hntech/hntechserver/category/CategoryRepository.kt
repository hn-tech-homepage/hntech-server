package hntech.hntechserver.category

import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByCategoryName(categoryName: String) : Category?
    fun existsByCategoryName(categoryName: String) : Boolean
    fun deleteByCategoryImagePath(categoryImagePath: String): Int
}