package hntech.hntechserver.archive

import hntech.hntechserver.category.CategoryException
import hntech.hntechserver.category.CategoryRepository
import hntech.hntechserver.file.FileService
import hntech.hntechserver.utils.logger
import org.springframework.stereotype.Service

@Service
class ArchiveService(
    private val archiveRepository: ArchiveRepository,
    private val categoryRepository: CategoryRepository,
    private val fileService: FileService,
    ) {
    val log = logger()

    /**
     * 자료 생성
     */
    fun createArchive(form: ArchiveRequest): ArchiveSimpleResponse {
        // 카테고리 가져오기
        val category = categoryRepository.findByCategoryName(form.archiveCategory)
            ?: throw CategoryException("해당 이름의 카테고리 조회 오류")

        // 해당 제품 찾기
        val item = category.products.filter { it.productName == form.itemType }[0]

        // 자료실 글 생성
        val archive = Archive(title = form.title, isNotice = form.isNotice, content = form.content, archiveCategory = category)
        archiveRepository.save(archive)

        // 파일 지정하기
        val saveArchiveFiles = fileService.saveAllFiles(form.files)
        archive.files = saveArchiveFiles

        return ArchiveSimpleResponse.createDto(archive, itemType = item.productName)
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