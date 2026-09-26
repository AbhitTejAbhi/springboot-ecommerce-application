package com.ecommerce.backend.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides a single, application-wide Cloudinary client as a Spring
 * bean. Credentials are externalized to application.properties (see
 * cloudinary.cloud-name / cloudinary.api-key / cloudinary.api-secret)
 * and never hardcoded.
 *
 * The Cloudinary SDK's own client is thread-safe and intended to be
 * reused across the application rather than constructed per-request,
 * so a single @Bean (Spring beans are singleton-scoped by default)
 * is the correct lifetime here.
 */
@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }
}