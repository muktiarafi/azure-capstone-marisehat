package dev.muktiarafi.marisehat.config;

import com.azure.identity.ClientSecretCredential;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.common.policy.RequestRetryOptions;
import com.azure.storage.common.policy.RetryPolicyType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.time.Duration;

@Configuration
@DependsOn("clientSecretCredential")
public class AzureBlobStorageConfig {

    @Bean
    public BlobServiceClientBuilder blobServiceClientBuilder(
            ClientSecretCredential clientSecretCredential,
            @Value("${app.config.azure.storage.endpoint}") String storageEndpoint
    ) {
        return new BlobServiceClientBuilder()
                .credential(clientSecretCredential)
                .endpoint(storageEndpoint);
    }

    @Bean
    public BlobServiceClient blobServiceClient(BlobServiceClientBuilder blobServiceClientBuilder) {
        return blobServiceClientBuilder.retryOptions(
                new RequestRetryOptions(
                        RetryPolicyType.EXPONENTIAL,
                        5,
                        Duration.ofSeconds(300L),
                        null,
                        null,
                        null)).buildClient();
    }
}
