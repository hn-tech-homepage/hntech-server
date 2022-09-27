package hntech.hntechserver.domain.product

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository: JpaRepository<Product, Long> {
    fun findFirstByOrderBySequenceDesc(): Product?
    fun existsByProductName(productName: String) : Boolean

    // 왼쪽에서 오른쪽으로 이동할 때
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Product p SET p.sequence = p.sequence - 1 WHERE p.sequence > :self AND p.sequence <= :target")
    fun adjustSequenceToLeft(@Param("self") self: Int, @Param("target") target: Int): Int

    // 오른쪽에서 왼쪽으로 이동할 때
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Product p SET p.sequence = p.sequence + 1 WHERE p.sequence > :target AND p.sequence < :self")
    fun adjustSequenceToRight(@Param("target") target: Int, @Param("self") self: Int): Int

    // 생성하기 전 모든 제품 sequence++
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Product p SET p.sequence = p.sequence + 1")
    fun adjustSequenceToRightAll(): Int

    // 삭제할 때 삭제되는 제품 기준 오른쪽 제품들 sequence--
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Product p SET p.sequence = p.sequence - 1 WHERE p.sequence > :self")
    fun adjustSequenceToLeftAll(@Param("self") self: Int): Int
}