package hntech.hntechserver.common

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api")
class GlobalController() {

//    @GetMapping("/")
//    fun hello() = "hello"

    @GetMapping("/error")
    fun error() = ResponseEntity.notFound()

    @GetMapping("/check-login")
    fun checkLoginState(request: HttpServletRequest): BoolResponse =
        if (request.session.getAttribute(ADMIN) != null) BoolResponse(true)
        else BoolResponse(false)
}

@Controller
class ReactClientRedirector {
    @GetMapping(value = [
        "",
        "/",
        "/company",
        "/product/**",
        "/productCategory/**",
        "/document",
        "/question/**",
        "/archive/**"
    ])
    fun redirect() = "forward:/index.html"
}