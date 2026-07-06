package com.ecommerce.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(servers())
                .externalDocs(externalDocumentation())
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME_NAME, bearerJwtScheme()));
    }

    private Info apiInfo() {
        return new Info()
                .title("Spring Boot E-Commerce Backend API")
                .description(
                        "Production-ready Spring Boot REST API supporting:\n\n"
                                + "- **Authentication** - JWT-based registration and login\n"
                                + "- **Role-Based Authorization** - ADMIN and CUSTOMER roles\n"
                                + "- **Product Catalog** - Full CRUD with Cloudinary image management\n"
                                + "- **Category Management** - Hierarchical product organization\n"
                                + "- **Shopping Cart** - Add, update, remove, and clear cart items\n"
                                + "- **Order Processing** - Atomic placement, status pipeline, cancellation with restock\n"
                                + "- **Payment Lifecycle** - Internal PENDING to SUCCESS/FAILED state machine\n"
                                + "- **Address Book** - Saved shipping addresses with single-default enforcement\n\n"
                                + "---\n\n"
                                + "**How to authenticate:**\n"
                                + "1. Call POST /api/auth/register or POST /api/auth/login\n"
                                + "2. Copy the token from the response\n"
                                + "3. Click Authorize (top right), paste the token, click Authorize\n"
                                + "4. All secured endpoints will now include your JWT automatically\n\n"
                                + "---\n\n"
                                + "Developer: Your Name | "
                                + "GitHub: https://github.com/your-github-username | "
                                + "LinkedIn: https://linkedin.com/in/your-linkedin-profile"
                )
                .version("1.0.0")
                .contact(apiContact())
                .license(apiLicense())
                .termsOfService("https://github.com/your-github-username/your-repo-name");
    }

    private Contact apiContact() {
        return new Contact()
                .name("Your Name")
                .email("your.email@example.com")
                .url("https://github.com/your-github-username");
    }

    private License apiLicense() {
        return new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
    }

    private ExternalDocumentation externalDocumentation() {
        return new ExternalDocumentation()
                .description("View full source code and documentation on GitHub")
                .url("https://github.com/your-github-username/your-repo-name");
    }

    private SecurityScheme bearerJwtScheme() {
        return new SecurityScheme()
                .name(BEARER_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .description(
                        "Paste your JWT here — obtained from POST /api/auth/login or "
                                + "POST /api/auth/register. Do not prefix with Bearer; "
                                + "Swagger UI adds that automatically."
                );
    }

    private List<Server> servers() {
        Server devServer = new Server()
                .url("http://localhost:8080")
                .description(" Local Development Server");

        // Production server will be added here once deployed
        // e.g. new Server().url("https://your-domain.com").description("Production Server")

        return List.of(devServer);
    }
}