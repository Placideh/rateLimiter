package com.placideh.rateLimiter.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.servlet.context-path:/api}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Bearer Authentication";
        final String apiKeySecuritySchemeName = "API Key Authentication";

        return new OpenAPI()
                .info(new Info()
                        .title("Rate Limiter API")
                        .description("API Rate Limiter System with Token Bucket and Fixed Window algorithms. " +
                                "Provides comprehensive rate limiting for SMS and Email notifications with " +
                                "admin controls, usage tracking, and tier-based pricing.")
                        .version("1.0.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080" + contextPath)
                                .description("Development Server")
                        ))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName)
                        .addList(apiKeySecuritySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token for authenticated endpoints (Admin & Client users)"))
                        .addSecuritySchemes(apiKeySecuritySchemeName, new SecurityScheme()
                                .name(apiKeySecuritySchemeName)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-Key")
                                .description("API Key for rate-limited notification endpoints")));
    }
}