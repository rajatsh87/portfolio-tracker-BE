package com.portfolio.portfolio_tracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI portfolioTrackerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Portfolio Tracker API")
                        .description("REST API documentation for the Enterprise Portfolio Tracking Engine.")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("admin@portfoliotracker.com")));
    }
}