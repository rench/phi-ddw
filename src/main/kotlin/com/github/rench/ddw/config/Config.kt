package com.github.rench.ddw.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.*


@Configuration
open class WebMvcConfiguration : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry?) {
        registry?.addMapping("/**");
    }
}

@Configuration
open class NullAuditorBean : AuditorAware<Long> {
    override fun getCurrentAuditor(): Optional<Long>? {
        return Optional.of(1L)
    }
}