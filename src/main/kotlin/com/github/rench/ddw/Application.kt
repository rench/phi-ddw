package com.github.rench.ddw

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
open class Application

/**
 * ddw application
 */
fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}