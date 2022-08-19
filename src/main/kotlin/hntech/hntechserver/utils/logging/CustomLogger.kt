package hntech.hntechserver.utils.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletRequest

inline fun <reified T> T.logger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

// request 헤더 정보들을 모아서 문자열로 만들어주는 함수
fun getHeadersAsString(request: HttpServletRequest): String {
    var headers = "headers = \n"
    request.headerNames.asIterator().forEach {
        headers += "$it : ${request.getHeader(it)}\n"
    }
    return headers
}