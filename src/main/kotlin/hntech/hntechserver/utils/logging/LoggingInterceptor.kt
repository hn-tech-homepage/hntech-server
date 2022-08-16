package hntech.hntechserver.utils.logging

import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class LoggingInterceptor(
    private val converter: PrettyConverter // NormalConverter, PrettyConverter 를 갈아끼우기만 하면 된다.
) : HandlerInterceptor {
    val log = logger()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        log.info(
            "-------------> [REQUEST] {} {} {}\n{}",
            request.remoteAddr,
            request.method,
            request.requestURL,
        )
        return super.preHandle(request, response, handler)
    }

    // 이래야 핸들러에서 예외가 발생해도 수행 됨
    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        val wrapResponse = response as ContentCachingResponseWrapper
        val body = converter.convert(wrapResponse.contentAsByteArray)
        log.info("<------------ [RESPONSE] {}\n{}", response.status, body)
        super.afterCompletion(request, response, handler, ex)
    }

}