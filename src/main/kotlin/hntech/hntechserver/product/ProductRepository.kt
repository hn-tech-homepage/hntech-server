package hntech.hntechserver.product

import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository: JpaRepository<Product, Long> {
    fun existsByProductName(productName: String) : Boolean
}