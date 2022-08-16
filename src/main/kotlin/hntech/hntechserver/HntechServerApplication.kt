package hntech.hntechserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
@EnableAspectJAutoProxy
class HntechServerApplication

fun main(args: Array<String>) {
	runApplication<HntechServerApplication>(*args)
}
