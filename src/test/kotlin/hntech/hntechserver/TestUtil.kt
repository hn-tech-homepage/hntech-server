package hntech.hntechserver

import hntech.hntechserver.category.CategoryCreateForm
import org.springframework.mock.web.MockMultipartFile


fun <T> logResult(actual: T, expected: T) =
    println("result\nactual \t\t: ${actual.toString()} \nexpected \t: ${expected.toString()}")

val testFile = MockMultipartFile(
    "file",
    "test.jpg",
    "image/jpeg",
    "test".byteInputStream()
)

val testCategories = listOf(
    CategoryCreateForm("카테고리1", testFile),
    CategoryCreateForm("카테고리2", testFile),
    CategoryCreateForm("카테고리3", testFile),
    CategoryCreateForm("카테고리4", testFile),
    CategoryCreateForm("카테고리5", testFile)
)


