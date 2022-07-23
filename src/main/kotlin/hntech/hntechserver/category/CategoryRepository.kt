package hntech.hntechserver.category

import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByCategoryName(categoryName: String) : Category?
}