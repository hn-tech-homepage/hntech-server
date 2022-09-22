package hntech.hntechserver.common

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
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
        "/company",
        "/client-product",
        "/product-detail",
        "/product-form",
        "/product-modify",
        "/productCategory-form",
        "/productCategory-modify",
        "/document",
        "/client-question",
        "/question-form",
        "/question-modify",
        "/question-detail",
        "/client-archive",
        "/archive-form",
        "/archive-detail",
        "/archive-modify"
    ])
    fun redirect() = "forward:/index.html"
}