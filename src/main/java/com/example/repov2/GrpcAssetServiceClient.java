package com.example.repov2;

import com.example.repov2.security.CustomTokenCallCredential;
import com.fico.platform.repository.v2alpha1.Asset;
import com.fico.platform.repository.v2alpha1.AssetServiceGrpc;
import com.fico.platform.repository.v2alpha1.CreateAssetRequest;
import com.fico.platform.repository.v2alpha1.CreateAssetResponse;
import com.fico.platform.repository.v2alpha1.DeleteAssetRequest;
import com.fico.platform.repository.v2alpha1.DeleteAssetResponse;
import com.fico.platform.repository.v2alpha1.GetAssetRequest;
import com.fico.platform.repository.v2alpha1.GetAssetResponse;
import com.fico.platform.repository.v2alpha1.ListAssetTypesRequest;
import com.fico.platform.repository.v2alpha1.ListAssetTypesResponse;
import com.fico.platform.repository.v2alpha1.ListAssetsRequest;
import com.fico.platform.repository.v2alpha1.ListAssetsResponse;
import com.fico.platform.repository.v2alpha1.UpdateAssetRequest;
import com.fico.platform.repository.v2alpha1.UpdateAssetResponse;
import com.google.protobuf.util.FieldMaskUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GrpcAssetServiceClient implements AssetServiceClient {

  private final ManagedChannel channel;
    AssetServiceGrpc.AssetServiceBlockingStub blockingStub;

  public GrpcAssetServiceClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port).useTransportSecurity().build());
  }

  public GrpcAssetServiceClient(ManagedChannel channel) {
    this.channel = channel;
    blockingStub = AssetServiceGrpc.newBlockingStub(channel);
  }

  @Override
  public CreateAssetResponse createAsset(String authToken, CreateAssetRequest createAssetRequest) {
    // Implementation
    try {
      // Create a request with authorization token in the headers
      CreateAssetResponse response =
          blockingStub.withCallCredentials(new CustomTokenCallCredential(authToken))
              .createAsset(createAssetRequest);

      return response;
    } catch (
        StatusRuntimeException e) {
      System.err.println("RPC failed: " + e.getStatus());
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public GetAssetResponse getAsset(String authToken, String assetName) {
    try {
      // Create a request with authorization token in the headers
      GetAssetRequest request = GetAssetRequest.newBuilder().setName(assetName).build();

      // Call the remote service with authorization token
      GetAssetResponse response =
          blockingStub.withCallCredentials(new CustomTokenCallCredential(authToken))
              .getAsset(request);

      return response;
    } catch (StatusRuntimeException e) {
      System.err.println("RPC failed: " + e.getStatus());
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public UpdateAssetResponse updateAsset(String authToken, UpdateAssetRequest updateAssetRequest) {
    try {

      // Call the remote service with authorization token
      UpdateAssetResponse response =
          blockingStub.withCallCredentials(new CustomTokenCallCredential(authToken))
              .updateAsset(updateAssetRequest);

      return response;
    } catch (StatusRuntimeException e) {
      System.err.println("RPC failed: " + e.getStatus());
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public DeleteAssetResponse deleteAsset(String authToken, DeleteAssetRequest deleteAssetRequest) {
    try {

      // Call the remote service with authorization token
      DeleteAssetResponse response =
          blockingStub.withCallCredentials(new CustomTokenCallCredential(authToken))
              .deleteAsset(deleteAssetRequest);

      return response;
    } catch (StatusRuntimeException e) {
      System.err.println("RPC failed: " + e.getStatus());
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public ListAssetTypesResponse listAssetTypes(String authToken, int pageSize) {
    return listAssetTypes(authToken, pageSize, null);
  }

  @Override
  public ListAssetTypesResponse listAssetTypes(String authToken, int pageSize, String pageToken) {
    try {
      ListAssetTypesRequest.Builder requestBuilder = ListAssetTypesRequest.newBuilder()
          .setPageSize(pageSize);

      if (pageToken != null) {
        requestBuilder.setPageToken(pageToken);
      }

      ListAssetTypesRequest request = requestBuilder.build();

      ListAssetTypesResponse response =
          blockingStub.withCallCredentials(new CustomTokenCallCredential(authToken))
              .listAssetTypes(request);

      // Print the response
      System.out.println("\n===========\nListAssetTypes Response: ");
      response.getAssetTypesList().stream().forEach(System.out::println);

      return response;
    } catch (StatusRuntimeException e) {
      System.err.println("RPC failed: " + e.getStatus());
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public ListAssetsResponse listAssets(String authToken, ListAssetsRequest listAssetsRequest) {
    try {

      ListAssetsResponse response =
          blockingStub.withCallCredentials(new CustomTokenCallCredential(authToken))
              .listAssets(listAssetsRequest);

      // Print the response
      System.out.println("\n===========\nListAssets Response: ");
      response.getAssetsList().stream().forEach(System.out::println);

      return response;
    } catch (StatusRuntimeException e) {
      System.err.println("RPC failed: " + e.getStatus());
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }
}
