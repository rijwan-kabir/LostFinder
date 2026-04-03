package com.diu.lostfinder.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Handle uploaded images
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/")
                .setCachePeriod(0);

        // Handle static resources
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}