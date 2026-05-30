package com.recipe.config;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.web.cors.CrossFilter;

@Configuration
public class CorsConfig {

    @Bean
    public Filter corsFilter() {
        return new CrossFilter()
                .allowedOrigins("*")
                .allowedMethods("GET,POST,PUT,DELETE,OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
