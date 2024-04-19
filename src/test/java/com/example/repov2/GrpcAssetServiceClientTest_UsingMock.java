package com.example.repov2;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.repov2.security.AuthTokenUtil;
import com.google.protobuf.FieldMask;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import org.junit.*;

import com.example.repov2.security.CustomTokenCallCredential;
import com.fico.platform.repository.v2alpha1.*;
import io.grpc.*;

public class GrpcAssetServiceClientTest_UsingMock {

    private GrpcAssetServiceClient grpcAssetServiceClient;
    private AssetServiceGrpc.AssetServiceBlockingStub mockBlockingStub;
    private ManagedChannel mockChannel;
    private String authToken;
    private static final String ASSET_TYPE_VERSION = "assetTypes/com.fico.repo.qa/versions/v1alpha1";

    @Before
    public void setUp() throws IOException {
        mockBlockingStub = mock(AssetServiceGrpc.AssetServiceBlockingStub.class);
        mockChannel = mock(ManagedChannel.class);
        grpcAssetServiceClient = new GrpcAssetServiceClient(mockChannel);
        grpcAssetServiceClient.blockingStub = mockBlockingStub;
        authToken = (new AuthTokenUtil()).fetchToken();

        // Ensure that the mock ManagedChannel doesn't return null when shutdown is called
        when(mockChannel.shutdown()).thenReturn(mock(ManagedChannel.class));


    }

    @After
    public void tearDown() throws InterruptedException {
        grpcAssetServiceClient.shutdown();
    }

    @Test
    public void testCreateAsset() {
        CreateAssetRequest createAssetRequest = CreateAssetRequest.newBuilder()
                .setAsset(Asset.newBuilder().setDescription("Asset as Tree1").setDisplayName("Asset One")
                        .setAssetTypeVersion(ASSET_TYPE_VERSION))
                .setRequestId(String.valueOf(UUID.randomUUID()))
                .build();

        CreateAssetResponse expectedResponse = CreateAssetResponse.newBuilder()
                .setAsset(Asset.newBuilder().setDescription("Test").setDisplayName("TestAsset").build())
                .build();

        when(mockBlockingStub.withCallCredentials(any(CustomTokenCallCredential.class)))
                .thenReturn(mockBlockingStub);
        when(mockBlockingStub.createAsset(createAssetRequest)).thenReturn(expectedResponse);

        CreateAssetResponse actualResponse = grpcAssetServiceClient.createAsset(authToken, createAssetRequest);

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testListAssets() {
        // Build the FieldMask for paths
        FieldMask.Builder fieldsBuilder = FieldMask.newBuilder();
        fieldsBuilder.addAllPaths(Collections.singletonList("display_name"));

        // Build the ListAssetsRequest
        ListAssetsRequest listAssetsRequest = ListAssetsRequest.newBuilder()
            .setParent("assetTypes/com.fico.repo.qa")
            .setFields(fieldsBuilder.build())
            //.setFilter("description = \"ListCheck Mar11\"")
            .setOrderBy("display_name")
            .setPageSize(30)
            //.setPageToken("")
            .build();

        ListAssetsResponse expectedResponse = ListAssetsResponse.newBuilder()
            .addAssets(0, Asset.newBuilder().setDescription("Test").setDisplayName("TestAsset").build())
            .build();

        when(mockBlockingStub.withCallCredentials(any(CustomTokenCallCredential.class)))
            .thenReturn(mockBlockingStub);
        when(mockBlockingStub.listAssets(listAssetsRequest)).thenReturn(expectedResponse);

        ListAssetsResponse actualResponse = grpcAssetServiceClient.listAssets(authToken, listAssetsRequest);
    }

    // Write similar tests for other methods like getAsset, updateAsset, deleteAsset, listAssetTypes
}
