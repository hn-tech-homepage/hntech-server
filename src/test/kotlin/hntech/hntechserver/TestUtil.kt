package hntech.hntechserver

import hntech.hntechserver.utils.config.FILE_SAVE_PATH_WINDOW_TEST
import hntech.hntechserver.utils.logger
import org.springframework.mock.web.MockMultipartFile
import java.io.File

class TestUtil {
    companion object {

        val log = logger()
        fun <T> logResult(actual: T, expected: T) =
            log.info("result\nactual \t\t: {} \nexpected \t: {}", actual.toString(), expected.toString())

        private val testFile = MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test".byteInputStream()
        )

        // 테스트 파일
        fun initTestFile(): MockMultipartFile = testFile

        // 스토리지안의 모든 파일 삭제
        fun deleteFiles() {
            val file = File(FILE_SAVE_PATH_WINDOW_TEST)
            file.listFiles().forEach { it.delete() }
        }
    }
}