package hntech.hntechserver.file

import hntech.hntechserver.TestUtil.Companion.logResult
import io.kotest.matchers.shouldBe
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

    @AfterEach
    // 단일 파일 삭제랑 다중 파일 삭제는 해당 메소드 실행여부로 검사 됨
    fun `로컬에 저장되었던 mock 파일들을 삭제한다`() =
        fileService.deleteAllFiles(fileRepository.findAll())


    @Test
    fun `단일 파일 저장 성공`() {
        val expected: File = fileService.saveFile(file)
        val actual: File = fileRepository.findByOriginalFilename("test.jpg")!!

        expected shouldBe actual
        logResult(actual, expected)
    }

    @Test
    fun `다중 파일 저장 성공`() {
        // given
        val files: MutableList<MultipartFile> = mutableListOf()
        for (i: Int in 0..10) { files.add(file) }

        // when
        val expected: MutableList<File> = fileService.saveAllFiles(files)
        val actual: MutableList<File> = fileRepository.findAll()

        // then
        expected shouldBe actual
        logResult(actual, expected)
    }

    @Test
    fun `단일 파일 수정 성공`() {
        // given
        val oldFileEntity: File = fileService.saveFile(file)
        val newFile = MockMultipartFile(
            "file",
            "test2.jpg",
            "image/jpeg",
            "test".byteInputStream()
        )

        // when
        val expected: File = fileService.updateFile(oldFileEntity, newFile)
        val actual: File = fileRepository.findByOriginalFilename("test2.jpg")!!

        // then
        expected.originalFilename shouldBe "test2.jpg"
        expected shouldBe actual
        logResult(actual, expected)
    }




}