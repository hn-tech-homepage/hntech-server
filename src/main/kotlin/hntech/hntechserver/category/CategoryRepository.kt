package hntech.hntechserver.category

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByCategoryName(categoryName: String) : Category?
    fun existsByCategoryName(categoryName: String) : Boolean
    fun findFirstByOrderBySequenceDesc(): Category?

    @Query("SELECT COUNT(c) FROM Category c WHERE c.showInMain = true")
    fun countMainCategories(): Int

    // 순서로 정렬된 카테고리 반환 [메인에 표시될 8개, 모든 카테고리]
    @Query("SELECT c from Category c WHERE c.showInMain = true ORDER BY c.sequence")
    fun findAllByShowInMain(): List<Category>
    fun findAllByOrderBySequence(): List<Category>

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Category c SET c.sequence = c.sequence - 1 WHERE c.sequence <= :sequence")
    fun adjustSequenceToLeft(@Param("sequence") sequence: Int): Int

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Category c SET c.sequence = c.sequence + 1 WHERE c.sequence >= :sequence")
    fun adjustSequenceToRight(@Param("sequence") sequence: Int): Int
}