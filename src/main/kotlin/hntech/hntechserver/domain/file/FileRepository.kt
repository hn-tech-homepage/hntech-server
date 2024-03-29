package hntech.hntechserver.domain.file

import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository : JpaRepository<File, Long> {
    fun findByServerFilename(serverFilename: String) : File?
}
