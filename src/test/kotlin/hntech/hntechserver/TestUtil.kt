package hntech.hntechserver

import hntech.hntechserver.category.CategoryCreateForm
import hntech.hntechserver.utils.config.FILE_SAVE_PATH_WINDOW_TEST
import hntech.hntechserver.utils.logger
import org.springframework.mock.web.MockMultipartFile
import java.io.File

class TestUtil {
    companion object {

        private val log = logger()
        fun <T> logResult(actual: T, expected: T) =
            log.info("result\nactual \t\t: {} \nexpected \t: {}", actual.toString(), expected.toString())

        private val testFile = MockMultipartFile(
            "file",
            "test.jpg",
            "image/jpeg",
            "test".byteInputStream()
        )

        private val testCategories = listOf(
            CategoryCreateForm("카테고리1", testFile),
            CategoryCreateForm("카테고리2", testFile),
            CategoryCreateForm("카테고리3", testFile),
            CategoryCreateForm("카테고리4", testFile),
            CategoryCreateForm("카테고리5", testFile)
        )

        // 테스트 파일
        fun initTestFile(): MockMultipartFile = testFile

        // 테스트 카테고리
        fun initTestCategories(): List<CategoryCreateForm> = testCategories

        // 스토리지안의 모든 파일 삭제
        fun deleteFiles() {
            val file = File(FILE_SAVE_PATH_WINDOW_TEST)
            file.listFiles().forEach { it.delete() }
        }
    }
}