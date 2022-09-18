package hntech.hntechserver.common

import hntech.hntechserver.utils.scheduler.EmailManager
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class GlobalController(private val emailManager: EmailManager) {

//    @GetMapping("/")
//    fun hello() = "hello"

    @GetMapping("/mail-test")
    fun sendMailTest() {
        emailManager.sendMail()
    }

    @GetMapping("/error")
    fun error() = ResponseEntity.notFound()

    @GetMapping("/check-login")
    fun checkLoginState(request: HttpServletRequest): BoolResponse =
        if (request.session.getAttribute(ADMIN) != null) BoolResponse(true)
        else BoolResponse(false)
}

//@Controller
//class WevController {
//    @GetMapping(value = [
//        "",
//        "/company",
//    "/product",
//    "/product-detail",
//    "/product-form",
//    "/product-modify",
//    "/productCategory-form",
//    "/productCategory-modify",
//    "/data",
//    "/question",
//    "/question-form",
//    "/question-modify",
//    "/question-detail",
//    "/archive-form",
//    "/archive-detail",
//    "/archive-modify"
//    ])
//    fun redirect() = "forward:/index.html"
//}