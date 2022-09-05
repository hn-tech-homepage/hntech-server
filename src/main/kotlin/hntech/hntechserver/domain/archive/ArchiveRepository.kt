package hntech.hntechserver.domain.archive

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ArchiveRepository : JpaRepository<Archive, Long> {

    @Query("SELECT a FROM Archive a WHERE a.notice = 'true'")
    fun findAllNotice(): List<Archive>

    @Query("SELECT a FROM Archive a WHERE a.notice = 'false'")
    override fun findAll(pageable: Pageable): Page<Archive>

    @Query("SELECT COUNT(a) FROM Archive a WHERE a.notice = 'true'")
    fun countNotice(): Long

    @Query("SELECT a FROM Archive a JOIN FETCH a.files")
    override fun findById(id: Long): Optional<Archive>
}

interface QArchiveRepository {
    fun searchArchive(pageable: Pageable, categoryName: String?, keyword: String?): Page<Archive>
}

