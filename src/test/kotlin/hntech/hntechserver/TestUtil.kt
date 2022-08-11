package hntech.hntechserver

import hntech.hntechserver.admin.Admin
import hntech.hntechserver.utils.config.ADMIN
import org.springframework.mock.web.MockHttpSession
import org.springframework.mock.web.MockMultipartFile


fun <T> logResult(actual: T, expected: T) =
    println("result\nactual \t\t: ${actual.toString()} \nexpected \t: ${expected.toString()}")

val testFile = MockMultipartFile(
    "file",
    "test.jpg",
    "image/jpeg",
    "test".byteInputStream()
)

val testFile2 = MockMultipartFile(
    "file",
    "test2.jpg",
    "image/jpeg",
    "test".byteInputStream()
)

// 테스트 파일
fun initTestFile(): MockMultipartFile = testFile

// 인증용 MockSession 생성
fun setMockSession(): MockHttpSession {
    val session = MockHttpSession()
    session.setAttribute(ADMIN, Admin(password = "1234"))
    return session
}
