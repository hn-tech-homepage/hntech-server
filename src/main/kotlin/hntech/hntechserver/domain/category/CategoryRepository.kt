package hntech.hntechserver.domain.category

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param


interface CategoryRepository : JpaRepository<Category, Long> {
    fun findByCategoryName(categoryName: String) : Category?
    fun existsByCategoryName(categoryName: String) : Boolean
    fun findFirstByOrderBySequenceDesc(): Category?

    @Query("SELECT COUNT(c) FROM Category c WHERE c.showInMain LIKE 'true'")
    fun countMainCategories(): Int

    // 순서로 정렬된 카테고리 반환 [메인에 표시될 8개, 모든 카테고리]
    @Query("SELECT c FROM Category c WHERE c.showInMain LIKE 'true' ORDER BY c.sequence")
    fun findAllByShowInMain(): List<Category>

    @Query("SELECT c FROM Category c WHERE c.type = :type ORDER BY c.sequence")
    fun findAllByType(@Param("type") type: String): List<Category>

    // 왼쪽에서 오른쪽으로 이동할 때
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Category c SET c.sequence = c.sequence - 1 WHERE c.sequence > :self AND c.sequence < :target")
    fun adjustSequenceToLeft(@Param("self") self: Int, @Param("target") target: Int): Int

    // 오른쪽에서 왼쪽으로 이동할 때
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Category c SET c.sequence = c.sequence + 1 WHERE c.sequence >= :target AND c.sequence < :self")
    fun adjustSequenceToRight(@Param("target") target: Int, @Param("self") self: Int): Int

    // 삭제할 때
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Category c SET c.sequence = c.sequence - 1 WHERE c.sequence > :self")
    fun adjustSequenceToLeftAll(@Param("self") self: Int): Int
}