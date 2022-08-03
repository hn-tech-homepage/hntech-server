package hntech.hntechserver.file

import hntech.hntechserver.utils.logger
import io.kotest.matchers.shouldBe
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
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
    // 단일 파일 삭제랑 다중 파일 삭제는 해당 메소드 실행여부로 검사 됨
    fun `로컬에 저장되었던 mock 파일들을 삭제한다`() =
        fileService.deleteAllFiles(fileRepository.findAll())


    @Test
    fun `단일 파일 저장 성공`() {
        // when
        val expected: File = fileService.saveFile(file)
        val actual: File = fileRepository.findByOriginFileName("test.jpg")!!

        // then
//        assertThat(actual).isEqualTo(expected)
        actual shouldBe  expected
        logResult(actual, expected)
    }

    @Test
    fun `다중 파일 저장 성공`() {
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

//    @Test @DisplayName("파일 단일 수정 성공")
//    fun updateFile() {
//        // given
//        val archive = Archive()
//        val files: MutableList<MultipartFile> = mutableListOf()
//        for (i: Int in 0..3) {
//            files.add(file)
//        }
//
//        // when
//        val expected: MutableList<ArchiveFile> = fileService.saveAllFiles(files, archive)
//        val actual = archiveFileRepository.findAll()
//
//
//        // then
////        assertThat(actual).isEqualTo(expected)
//        assertThat(archive.files.size).isEqualTo(files.size)
//        logResult(actual, expected)
//    }




}