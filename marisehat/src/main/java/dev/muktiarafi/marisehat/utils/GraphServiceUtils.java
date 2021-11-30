package dev.muktiarafi.marisehat.utils;

import com.azure.identity.ClientSecretCredential;
import com.microsoft.graph.authentication.BaseAuthenticationProvider;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class GraphServiceUtils {
    private final String clientId;
    private final String clientSecret;
    private final String tenantId;
    private final List<String> scopes;
    private final ClientSecretCredential clientSecretCredential;

    public GraphServiceUtils(
            @Value("${azure.activedirectory.client-id}") String clientId,
            @Value("${azure.activedirectory.client-secret}") String clientSecret,
            @Value("${azure.activedirectory.tenant-id}") String tenantId,
            @Value("${azure.activedirectory.authorization-clients.graph.scopes}") List<String> scopes,
            ClientSecretCredential clientSecretCredential
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tenantId = tenantId;
        this.scopes = scopes;
        this.clientSecretCredential = clientSecretCredential;
    }

    public GraphServiceClient client() {
        var tokenCredentialAuthProvider = new TokenCredentialAuthProvider(scopes, clientSecretCredential);

        return GraphServiceClient.builder().authenticationProvider(tokenCredentialAuthProvider)
                .buildClient();
    }

    public GraphServiceClient client(OAuth2AuthorizedClient auth2AuthorizedClient) {
        return GraphServiceClient.builder().authenticationProvider(new GraphAuthenticationProvider(auth2AuthorizedClient))
                .buildClient();
    }

    @AllArgsConstructor
    public static class GraphAuthenticationProvider extends BaseAuthenticationProvider {

        private OAuth2AuthorizedClient auth2AuthorizedClient;

        @Override
        public CompletableFuture<String> getAuthorizationTokenAsync(URL url) {
            return CompletableFuture.completedFuture(auth2AuthorizedClient.getAccessToken().getTokenValue());
        }
    }
}
