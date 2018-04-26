package com.github.rench.ddw

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
open class Application

/**
 * ddw application
 */
fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}