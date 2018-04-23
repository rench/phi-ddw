package com.github.rench.ddw.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
open class WebMvcConfiguration : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry?) {
        registry?.addMapping("/**");
    }
}