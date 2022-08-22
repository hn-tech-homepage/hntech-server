package hntech.hntechserver.domain.file

import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository : JpaRepository<File, Long> {
    fun findByOriginalFilename(originalFilename: String) : File?
    fun findByServerFilename(serverFilename: String) : File?
}
