package hntech.hntechserver.utils.logging

import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class LoggingInterceptor(
    private val converter: PrettyConverter // NormalConverter, PrettyConverter 를 갈아끼우기만 하면 된다.
) : HandlerInterceptor {
    val log = logger()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // form-data 를 담는 request 객체는 타입 캐스팅을 할 수 없어서 처리해준다
//        if (
//            !request.contentType.startsWith("multipart/form-data") &&
//            request.method.startsWith("POST") &&
//            request.contentType.isNotEmpty()
//        ) {
//            val wrapRequest = request as MultiAccessRequestWrapper
//            val body = converter.convert(wrapRequest.getContents())
//            log.info(
//                "-------------> [REQUEST] {} {} {} BODY\n{}",
//                request.remoteAddr,
//                request.method,
//                request.requestURL,
//                body
//            )
//        } else {
//            log.info(
//                "-------------> [REQUEST] {} {} {}",
//                request.remoteAddr,
//                request.method,
//                request.requestURL,
//            )
//        }
        return super.preHandle(request, response, handler)
    }

    // 이래야 핸들러에서 예외가 발생해도 수행 됨
    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
//        val wrapResponse = response as ContentCachingResponseWrapper
//        val body = converter.convert(wrapResponse.contentAsByteArray)
//        log.info("<------------ [RESPONSE] {} JSON {}", response.status, body)
        super.afterCompletion(request, response, handler, ex)
    }

}