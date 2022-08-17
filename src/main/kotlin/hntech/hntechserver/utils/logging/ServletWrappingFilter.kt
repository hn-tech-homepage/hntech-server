package hntech.hntechserver.utils.logging

import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 서블릿 request 랑 response 객체는 단 한번만 읽을 수 있도록 설정되어 있어서,
 * 여러번 읽기 위해서는 캐싱된 객체로 래핑을 해줘야함.
 * 구조상 이를 필터 부분에서 해줘야 함
 */

@Component
class ServletWrappingFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val wrapRequest = ContentCachingRequestWrapper(request)
        val wrapResponse = ContentCachingResponseWrapper(response)
        filterChain.doFilter(wrapRequest, wrapResponse)
        wrapResponse.copyBodyToResponse() // 이 부분이 핵심이다. 이를 통해 response 를 다시 읽을 수 있다.
    }

}