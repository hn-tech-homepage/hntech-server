//package hntech.hntechserver.auth
//
//import hntech.hntechserver.config.ADMIN
//import hntech.hntechserver.config.AUTH_DENIED
//import org.springframework.web.method.HandlerMethod
//import org.springframework.web.servlet.HandlerInterceptor
//import javax.security.auth.login.LoginException
//import javax.servlet.http.HttpServletRequest
//import javax.servlet.http.HttpServletResponse
//
//class LoginCheckInterceptor : HandlerInterceptor {
//    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
//        if (handler is HandlerMethod) {
//            // LoginCheck 어노테이션이 없는 컨트롤러 핸들러 메소드는 그냥 true 를 발생함 (전부다 접근 가능)
//            handler.getMethodAnnotation(Auth::class.java) ?: return true
//        }
//
//        // LoginCheck 어노테이션이 있으면 세션을 검사한다
//        if (request.session.getAttribute(ADMIN) == null)
//            throw LoginException(AUTH_DENIED)
//        return true
//    }
//}