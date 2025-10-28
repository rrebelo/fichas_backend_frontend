package com.example.cmderma.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Student Management API",
                description = "Operations for managing student records",
                version = "v1",
                contact = @Contact(name = "Cmderma Team"),
                license = @License(name = "Apache 2.0")
        )
)
@Configuration
public class OpenApiConfig {
}
