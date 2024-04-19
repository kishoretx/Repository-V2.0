package com.example.repov2;

import com.example.repov2.security.AuthTokenUtil;
import com.example.repov2.util.Utility_JsonToStructConverter;
import com.fico.platform.repository.v2alpha1.Asset;
import com.fico.platform.repository.v2alpha1.CreateAssetRequest;
import com.fico.platform.repository.v2alpha1.CreateAssetResponse;
import com.fico.platform.repository.v2alpha1.DeleteAssetRequest;
import com.fico.platform.repository.v2alpha1.DeleteAssetResponse;
import com.fico.platform.repository.v2alpha1.GetAssetResponse;
import com.fico.platform.repository.v2alpha1.ListAssetTypesResponse;
import com.fico.platform.repository.v2alpha1.ListAssetsRequest;
import com.fico.platform.repository.v2alpha1.ListAssetsResponse;
import com.fico.platform.repository.v2alpha1.StructuredContent;
import com.fico.platform.repository.v2alpha1.StructuredContentOrBuilder;
import com.fico.platform.repository.v2alpha1.UpdateAssetRequest;
import com.fico.platform.repository.v2alpha1.UpdateAssetResponse;
import com.google.protobuf.Descriptors;
import com.google.protobuf.FieldMask;
import com.google.protobuf.Message;
import com.google.protobuf.Struct;
import com.google.protobuf.util.FieldMaskUtil;
import com.google.protobuf.util.JsonFormat;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.ClassPathResource;

public class Main {
  private static final String SERVER_HOST = "assets.dms.dev.usw2.ficoanalyticcloud.com";
  private static final int SERVER_PORT = 443;
  private static final String ASSET_TYPE_VERSION = "assetTypes/com.fico.repo.qa/versions/v1alpha1";

  public static void main(String[] args) throws Exception {
    AuthTokenUtil authTokenUtil = new AuthTokenUtil();
    String authToken = authTokenUtil.fetchToken();

    AssetServiceClient client = new GrpcAssetServiceClient(SERVER_HOST, SERVER_PORT);

    try {

      //1. ****** Building CreateAssetRequest from scratch. StructuredContent from JSON file. *****
      String jsonString = "{\"city\":\"Berlin\",\"country\":\"Germany\"}";
      Struct struct = Utility_JsonToStructConverter.convertJsonToStruct(jsonString);

      StructuredContent structuredContent =StructuredContent.newBuilder().setContent(struct).build();

      CreateAssetRequest createAssetRequest1111 = CreateAssetRequest.newBuilder().setAsset(
          Asset.newBuilder().setDisplayName("Display Name").setDescription("Asset Desc")
              .setAssetTypeVersion(ASSET_TYPE_VERSION)
              .setStructuredContent(structuredContent))
          .setRequestId(String.valueOf(UUID.randomUUID()))
          .build();

      CreateAssetResponse createAssetResponse1 = client.createAsset(authToken, createAssetRequest1111);
      System.out.println("1. ===== Creating asset by building Struct:" + createAssetResponse1.toString());


      //2. ****** Building CreateAssetRequest from JSON file. *****
      CreateAssetRequest.Builder builder = CreateAssetRequest.newBuilder();
      builder.setRequestId(UUID.randomUUID().toString());
      JsonFormat.parser()
          .merge(new InputStreamReader(new ClassPathResource("createAsset.json").getInputStream()), builder);

      builder.setAsset(builder.getAsset().toBuilder().clearRemarkComment()
          .setRemarkComment("Remark kkk").build());

      CreateAssetRequest createAssetRequest = builder.build();

      CreateAssetResponse createAssetResponse = client.createAsset(authToken, createAssetRequest);

      System.out.println("2. ===== Creating asset from JSON payload:" + createAssetResponse.toString());



      //3. ****** GetAsset *****
      String assetName = createAssetResponse.getAsset().getName();
      String etag = createAssetResponse.getAsset().getEtag();

      GetAssetResponse getAssetResponse = client.getAsset(authToken, assetName);

      System.out.println("3. ===== GetAsset:" + getAssetResponse.toString());

      //4. ****** UpdateAsset *****
      UpdateAssetRequest updateAssetRequest = UpdateAssetRequest.newBuilder().setRequestId(
              String.valueOf(UUID.randomUUID())).setAsset(Asset.newBuilder()
              .setDisplayName("Update this asset")
              .setDescription("Update this desc").build()).setEtag(etag)
          .setUpdateMask(FieldMaskUtil.fromStringList(
          List.of("description", "display_name")))
          .build();


      UpdateAssetResponse updateAssetResponse = client.updateAsset(authToken, updateAssetRequest);
      System.out.println("4. ===== UpdateAsset:" + updateAssetResponse.toString());

      //5. ****** DeleteAsset *****
      etag = updateAssetResponse.getAsset().getEtag();
      DeleteAssetRequest deleteAssetRequest = DeleteAssetRequest.newBuilder()
          .setRequestId(String.valueOf(UUID.randomUUID()))
          .setName(assetName)
          .setEtag(etag)
          .build();
      DeleteAssetResponse deleteAssetResponse = client.deleteAsset(authToken, deleteAssetRequest);
      System.out.println("5. ===== DeleteAsset:" + deleteAssetResponse.toString());


      //6. ****** ListAssetTypes *****
      ListAssetTypesResponse listAssetTypesResponse = client.listAssetTypes(authToken, 20);
      System.out.println("6. ===== ListAssetTypes:" + listAssetTypesResponse.toString());


      //7. ****** ListAssets *****
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

      ListAssetsResponse listAssetsResponse = client.listAssets(authToken, listAssetsRequest);
      System.out.println("7. ===== ListAssets:");
      listAssetsResponse.getAssetsList().stream().forEach(System.out::println);

    } finally {
      client.shutdown();
    }
  }
}
