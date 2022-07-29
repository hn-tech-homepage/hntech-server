package hntech.hntechserver.admin

import org.springframework.data.jpa.repository.JpaRepository

interface AdminRepository : JpaRepository<Admin, Long> {
    fun findByPassword(password: String) : Admin?
}