package ru.avg.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for web-level settings in the application.
 * This class enables Spring MVC and configures Cross-Origin Resource Sharing (CORS)
 * to allow controlled access to server endpoints from specific origins defined in configuration properties.
 * <p>
 * The primary purpose of this configuration is to permit frontend applications to communicate
 * with the backend API without being blocked by browser same-origin policy, while supporting
 * environment-specific CORS rules via externalized configuration.
 * </p>
 * <p>
 * CORS is configured only for paths under {@code /approval/**}, ensuring that
 * cross-origin access is limited to specific endpoints. Additional security measures include
 * wildcard header support, preflight request caching, and no explicit credential allowance
 * unless specified.
 * </p>
 *
 * @author AVG
 * @see WebMvcConfigurer
 * @see EnableWebMvc
 * @see Configuration
 * @see Value
 * @since 1.0
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    /**
     * Array of allowed origins for CORS requests, injected from configuration properties.
     * This allows different frontend domains to be specified per environment:
     * <ul>
     *   <li>Development: {@code http://localhost:5173} (Vue/Vite)</li>
     * </ul>
     * <p>
     * Configured via {@code application.properties} or environment variables using key {@code cors.origins}.
     * Example:
     * <pre>
     * cors:
     *   origins: http://localhost:5173
     * </pre>
     * </p>
     */
    @Value("${cors.origins}")
    private String[] corsOrigins;

    /**
     * Configures CORS mappings for the application.
     * <p>
     * This method defines which endpoints are accessible from cross-origin requests,
     * specifying the allowed origins, HTTP methods, headers, and preflight cache duration.
     * </p>
     * <p>
     * Current configuration:
     * <ul>
     *   <li><strong>Path Pattern:</strong> {@code /approval/**} — applies only to approval module endpoints</li>
     *   <li><strong>Allowed Origins:</strong> Dynamically loaded from {@code cors.origins} property</li>
     *   <li><strong>Allowed Methods:</strong> GET, POST, PUT, DELETE — full CRUD support</li>
     *   <li><strong>Allowed Headers:</strong> All ({@code "*"}) — supports custom and standard headers</li>
     *   <li><strong>Max Age:</strong> 3600 seconds (1 hour) — caches preflight (OPTIONS) responses for performance</li>
     * </ul>
     * </p>
     * <p>
     * Note: Credentials (e.g., cookies, Authorization headers) are not explicitly allowed.
     * To enable them, add {@code .allowCredentials(true)} and ensure frontend sets {@code withCredentials = true}.
     * </p>
     *
     * @param registry the {@link CorsRegistry} used to register CORS configurations
     * @see CorsRegistry
     * @see #corsOrigins
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/approval/**")
                .allowedOrigins(corsOrigins)
                .allowedMethods("PUT", "DELETE", "GET", "POST", "PATCH")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}