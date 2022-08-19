package hntech.hntechserver.utils.logging

import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


/**
 * Logging 인터셉터 : 모든 요청에 대한 정보를 기록한다.
 */
@Component
class StandardLoggingInterceptor : HandlerInterceptor {
    val log = logger()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        log.info("{}", getHeadersAsString(request))
        log.info(
            "-------------- User Request {} -------------- {}: {}",
            request.remoteAddr,
            request.method,
            request.requestURL
        )
        return super.preHandle(request, response, handler)
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        log.info("------------ Server Response ------------- result : {}", response.status)
        super.afterCompletion(request, response, handler, ex)
    }
}