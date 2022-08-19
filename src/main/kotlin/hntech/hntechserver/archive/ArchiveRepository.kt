package hntech.hntechserver.archive

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ArchiveRepository : JpaRepository<Archive, Long> {

    @Query("SELECT a FROM Archive a WHERE a.notice = 'true'")
    fun findAllNotice(): List<Archive>

    @Query("SELECT COUNT(a) FROM Archive a WHERE a.notice = 'true'")
    fun countNotice(): Long
}



