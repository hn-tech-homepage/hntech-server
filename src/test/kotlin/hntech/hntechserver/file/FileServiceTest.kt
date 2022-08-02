package hntech.hntechserver.file

import hntech.hntechserver.archive.Archive
import hntech.hntechserver.utils.logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@SpringBootTest
@Transactional
class FileServiceTest {
    @Autowired lateinit var fileService: FileService
    @Autowired lateinit var fileRepository: FileRepository
    @Autowired lateinit var archiveFileRepository: ArchiveFileRepository

    val file = MockMultipartFile(
        "file",
        "test.jpg",
        "image/jpeg",
        "test".byteInputStream()
    )

    val log = logger()
    fun <T> logResult(actual: T, expected: T) {
        log.info("result\nactual \t\t: {} \nexpected \t: {}", actual.toString(), expected.toString())
    }

    @AfterEach
    fun deleteAllFiles() =
        fileService.deleteAllFiles(fileRepository.findAll())


    @Test @DisplayName("단일 파일 저장 성공")
    fun saveFile() {
        // when
        val expected: File = fileService.saveFile(file)
        val actual: File = fileRepository.findByOriginFileName("test.jpg")!!

        // then
        assertThat(actual).isEqualTo(expected)
        logResult(actual, expected)
    }

    @Test @DisplayName("다중 파일 저장 성공")
    fun saveAllFiles() {
        // given
        val files: MutableList<MultipartFile> = mutableListOf()
        for (i: Int in 0..10) {
            files.add(file)
        }

        // when
        val expected: MutableList<File> = fileService.saveAllFiles(files)
        val actual: MutableList<File> = fileRepository.findAll()

        // then
        assertThat(actual).isEqualTo(expected)
        logResult(actual, expected)
    }

    @Test @DisplayName("자료실 파일 전체 저장 성공")
    fun saveAllArchiveFiles() {
        // given
        val archive = Archive()
        val files: MutableList<MultipartFile> = mutableListOf()
        for (i: Int in 0..3) {
            files.add(file)
        }

        // when
        val expected: MutableList<ArchiveFile> = fileService.saveAllFiles(files, archive)
        val actual = archiveFileRepository.findAll()


        // then
//        assertThat(actual).isEqualTo(expected)
        assertThat(archive.files.size).isEqualTo(files.size)
        logResult(actual, expected)
    }




}