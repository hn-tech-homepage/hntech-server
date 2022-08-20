package hntech.hntechserver.utils.function

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// request 헤더 정보들을 모아서 문자열로 만들어주는 함수
fun HttpServletRequest.getHeadersAsString(): String {
    var headers = "headers = { "
    this.headerNames.asIterator().forEach {
        headers += "$it : ${this.getHeader(it)}, "
    }
    headers += "}"
    return headers
}

fun HttpServletResponse.getHeadersAsString(): String {
    var headers = "headers = { "
    this.headerNames.forEach {
        headers += "$it : ${this.getHeader(it)}, "
    }
    headers += "}"
    return headers
}