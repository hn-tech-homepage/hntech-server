package hntech.hntechserver.file

import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository : JpaRepository<File, Long> {
    fun findByOriginFileName(originFilename: String) : File?
//    fun findBySavedPath(savedPath: String) : File?
//    fun deleteBySavedPath(savedPath: String) : Int

//    @Query("SELECT f FROM File f WHERE f.type = 'archive'")
//    fun findAllByArchive() : List<ArchiveFile>
}

interface ArchiveFileRepository : JpaRepository<ArchiveFile, Long> {
}

interface ProductFileRepository: JpaRepository<ProductFile, Long> {
}