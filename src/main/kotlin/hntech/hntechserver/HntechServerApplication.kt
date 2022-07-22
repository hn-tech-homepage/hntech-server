package hntech.hntechserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HntechServerApplication

fun main(args: Array<String>) {
	runApplication<HntechServerApplication>(*args)
}
