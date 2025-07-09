package com.dosion.noisense.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// http://localhost:8080/swagger-ui/index.html
@Configuration
public class SwaggerConfig {
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info().title("NoiSense API")
            .description("NoiSense Application API Documentation")
            .version("v1.0"));
  }
/*    // Security Scheme 정의
    SecurityScheme securityScheme = new SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
        .in(SecurityScheme.In.HEADER)
        .name("Authorization");

    // Security Requirement 정의
    SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

    return new OpenAPI()
        .info(new Info().title("Todolisst API")
            .description("Todolist Application API Documentation")
            .version("v1.0"))
        .addSecurityItem(securityRequirement)  // Security Requirement 추가
        .schemaRequirement("BearerAuth", securityScheme);  // Security Scheme 추가
  }*/
}
