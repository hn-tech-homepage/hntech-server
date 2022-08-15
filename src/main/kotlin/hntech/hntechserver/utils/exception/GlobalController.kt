package hntech.hntechserver.utils.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GlobalController {

    @GetMapping
    fun hello() = "HNTECH REST API server is running"

    @GetMapping("/error")
    fun error() = ResponseEntity.notFound()
}