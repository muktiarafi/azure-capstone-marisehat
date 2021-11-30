package dev.muktiarafi.marisehat.config;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureCredential {

    @Bean
    public ClientSecretCredential clientSecretCredential(
            @Value("${azure.activedirectory.client-id}") String clientId,
            @Value("${azure.activedirectory.client-secret}") String clientSecret,
            @Value("${azure.activedirectory.tenant-id}") String tenantId
    ) {
        return new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tenantId(tenantId)
                .build();
    }
}
