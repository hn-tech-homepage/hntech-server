package hntech.hntechserver.archive

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ArchiveRepository : JpaRepository<Archive, Long> {

    @Query("SELECT a FROM Archive a WHERE a.notice = 'true'")
    fun findAllNotice(): List<Archive>

    @Query("SELECT a FROM Archive a WHERE a.notice = 'false'")
    override fun findAll(pageable: Pageable): Page<Archive>

    @Query("SELECT COUNT(a) FROM Archive a WHERE a.notice = 'true'")
    fun countNotice(): Long
}

interface ArchiveFileRepository : JpaRepository<ArchiveFile, Long> {}



