package dev.muktiarafi.marisehat.utils;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.models.UserDelegationKey;
import com.azure.storage.blob.sas.BlobContainerSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.sas.SasProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class StorageUtils {
    private final String containerName;
    private final String storageEndpoint;
    private final BlobServiceClient blobServiceClient;

    public StorageUtils(
            @Value("${app.config.azure.storage.container-name}") String containerName,
            @Value("${app.config.azure.storage.endpoint}") String storageEndpoint,
            BlobServiceClient blobServiceClient) {
        this.containerName = containerName;
        this.storageEndpoint = storageEndpoint;
        this.blobServiceClient = blobServiceClient;
    }

    public BlobClient getBlobClient(String blobName) {
        var containerClient = blobServiceClient.getBlobContainerClient(containerName);

        return containerClient.getBlobClient(blobName);
    }

    public void upload(String blobName, byte[] data) {
        var blobClient = getBlobClient(blobName);

        blobClient.upload(BinaryData.fromBytes(data));
        blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(MediaType.APPLICATION_PDF_VALUE));
    }

    public void upload(BlobClient blobClient, byte[] data) {
        blobClient.upload(BinaryData.fromBytes(data));
        blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType(MediaType.APPLICATION_PDF_VALUE));
    }

    public UserDelegationKey generateUserDelegationKey() {
        var keyStart = OffsetDateTime.now();
        var keyExpiry = keyStart.plusMinutes(30);

        return blobServiceClient.getUserDelegationKey(keyStart, keyExpiry);
    }

    public String generateSasToken(BlobClient blobClient, UserDelegationKey userDelegationKey) {
        var blobContainerSasPermission = new BlobContainerSasPermission()
                .setReadPermission(true);
        var builder = new BlobServiceSasSignatureValues(
                userDelegationKey.getSignedExpiry(), blobContainerSasPermission)
                .setProtocol(SasProtocol.HTTPS_ONLY);

        return String.format("%s/%s/%s?%s",
            storageEndpoint,
                blobClient.getContainerName(),
                blobClient.getBlobName(),
                blobClient.generateUserDelegationSas(builder, userDelegationKey)
        );
    }
}
