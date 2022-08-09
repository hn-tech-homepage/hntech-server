package hntech.hntechserver

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

