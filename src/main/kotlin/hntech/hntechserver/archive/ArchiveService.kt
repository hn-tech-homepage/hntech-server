package hntech.hntechserver.archive

import hntech.hntechserver.CategoryException
import hntech.hntechserver.category.CategoryRepository
import hntech.hntechserver.file.File
import hntech.hntechserver.file.FileRepository

import hntech.hntechserver.utils.logger
import org.hibernate.boot.archive.spi.ArchiveException
import org.springframework.stereotype.Service

@Service
class ArchiveService(
    private val archiveRepository: ArchiveRepository,
    private val categoryRepository: CategoryRepository,
    private val fileRepository: FileRepository,
    ) {
    val log = logger()

    /**
     * 자료 생성
     */
    fun createArchive(form: ArchiveRequest): ArchiveSimpleResponse {
        // 카테고리 가져오기
        val category = categoryRepository.findByCategoryName(form.archiveCategory)
            ?: throw CategoryException("해당 이름의 카테고리 조회 오류")

        // 파일 지정하기
        val files: MutableList<File> = mutableListOf()
        form.filesWithOriginFilename.forEach {
            val file: File = fileRepository.findByOriginFileName(it) ?: throw ArchiveException("자료실 파일 조회 오류")
            files.add(file)
        }

        // 해당 제품 찾기
        val item = category.items.filter { it.itemName == form.itemType }[0]

        // 자료실 글 생성
        val archive = Archive(title = form.title, isNotice = form.isNotice, content = form.content, archiveCategory = category, files = files, )
        archiveRepository.save(archive)

        return ArchiveSimpleResponse.createDto(archive, itemType = item.itemName)
    }

    /**
     * 자료 조회
     */

    /**
     * 자료 수정
     */

    /**
     * 자료 삭제
     */
}