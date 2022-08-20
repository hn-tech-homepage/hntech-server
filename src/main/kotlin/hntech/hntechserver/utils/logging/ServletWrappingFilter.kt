package hntech.hntechserver.utils.logging

import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
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
    // OncePerRequestFilter 는 모든 서블릿 요청에 일관된 필터를 적용하기 위해서 사용한다.

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val wrapRequest = MultiAccessRequestWrapper(request) // 커스텀으로 생성한 Wrapper
        val wrapResponse = ContentCachingResponseWrapper(response)
        filterChain.doFilter(wrapRequest, wrapResponse)
        wrapResponse.copyBodyToResponse()
    }

}