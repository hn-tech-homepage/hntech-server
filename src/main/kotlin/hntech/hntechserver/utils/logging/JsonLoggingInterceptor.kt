package hntech.hntechserver.utils.logging

import hntech.hntechserver.utils.function.getHeadersAsString
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JsonLoggingInterceptor(
    private val converter: PrettyConverter // NormalConverter, PrettyConverter 를 갈아끼우기만 하면 된다.
) : HandlerInterceptor {
    val log = logger()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        log.info(
            "-------------> [REQUEST] {} {} {} {}",
            request.remoteAddr,
            request.method,
            request.requestURL,
            request.getHeadersAsString(),
        )

        if (request.contentType != null &&
            request.contentType.startsWith("application/json") &&
            (request.method.startsWith("POST") ||
            request.method.startsWith("PUT")) &&
            request is MultiAccessRequestWrapper){
            val body = converter.convert(request.getContents())
            log.info("BODY\n$body")
        }

        if (request.contentType != null &&
                request.contentType.startsWith("multipart/form-data") &&
                request.method.startsWith("POST") &&
                request is MultiAccessRequestWrapper) {
            val body = request.getContents().toString()
            log.info("BODY\n$body")
        }

        return super.preHandle(request, response, handler)
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        log.info("<------------ [RESPONSE] {} response {}", response.status, response.getHeadersAsString())

        if (response.contentType != null &&
            response.contentType.startsWith("application/json") &&
            response is ContentCachingResponseWrapper) {
            val body = converter.convert(response.contentAsByteArray)
            log.info("BODY\n$body")
        }

        super.afterCompletion(request, response, handler, ex)
    }

}