package hntech.hntechserver

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import hntech.hntechserver.domain.admin.Admin
import hntech.hntechserver.config.ADMIN
import org.springframework.mock.web.MockHttpSession
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MvcResult


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

// mockMvc response JSON 결과를 보기좋게 출력
fun jsonPrint(result: MvcResult) {
    // 한글 깨짐 처리
    val body = String(result.response.contentAsString
        .toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)

    // GSON 을 이용한 JSON Pretty Print
    val gson = GsonBuilder().setPrettyPrinting().create()
    println(gson.toJson(JsonParser().parse(body)))
}
