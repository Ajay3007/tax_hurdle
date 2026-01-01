package com.investinghurdle.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;

/**
 * InvestingHurdle REST API Application
 * 
 * Main Spring Boot application providing REST API for tax calculations
 * and portfolio management.
 * 
 * @author InvestingHurdle Team
 * @version 1.0.0
 */
@SpringBootApplication
public class InvestingHurdleApiApplication {

    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("  InvestingHurdle REST API");
        System.out.println("  Starting server...");
        System.out.println("=".repeat(70));
        
        SpringApplication.run(InvestingHurdleApiApplication.class, args);
    }

    /**
     * Configure CORS to allow frontend applications to access the API
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000", "http://localhost:4200") // React, Angular default ports
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }

    /**
     * Configure OpenAPI/Swagger documentation
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("InvestingHurdle Tax Calculator API")
                        .version("1.0.0")
                        .description("REST API for Indian equity tax calculations (STCG, LTCG, Speculation)")
                        .contact(new Contact()
                                .name("InvestingHurdle")
                                .email("support@investinghurdle.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
