package com.github.rench.ddw

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class Application

/**
 * ddw application
 */
fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}