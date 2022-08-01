package hntech.hntechserver.utils

import hntech.hntechserver.utils.config.ADMIN
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.security.auth.login.LoginException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

class LoginCheckInterceptor : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val session: HttpSession = request.session
        if (session.getAttribute(ADMIN) == null)
            throw LoginException("인증 거부 : 허가되지 않은 사용자")
        return true
    }
}

class LoggingInterceptor : HandlerInterceptor {
    val log = logger()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        log.info(
            "-------------- User Request {} -------------- {}: {}",
            request.remoteAddr,
            request.method,
            request.requestURL
        )
        return super.preHandle(request, response, handler)
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
        log.info("------------ Server Response ------------- result : {}", response.status)
        super.postHandle(request, response, handler, modelAndView)
    }
}