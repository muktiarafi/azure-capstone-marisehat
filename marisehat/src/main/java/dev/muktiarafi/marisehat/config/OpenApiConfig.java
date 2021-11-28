package dev.muktiarafi.marisehat.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    private final String authorizationUrl;

    public OpenApiConfig(
            @Value("${springdoc.oAuthFlow.authorizationUrl}") String authorizationUrl
    ) {
        this.authorizationUrl = authorizationUrl;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("MariSehat API").version("V1"))
                .components(new Components()
                        .addSecuritySchemes("OAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .flows(new OAuthFlows().implicit(oAuthFlow()))))
                .addSecurityItem(new SecurityRequirement().addList("OAuth"));
    }

    private OAuthFlow oAuthFlow() {
        return new OAuthFlow()
                .authorizationUrl(authorizationUrl);
    }
}
