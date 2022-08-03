package hntech.hntechserver.file

import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository : JpaRepository<File, Long> {
    fun findByOriginFileName(originFilename: String) : File?
}
