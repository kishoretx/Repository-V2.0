package com.example.repov2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.repov2.security.AuthTokenUtil;
import com.example.repov2.util.Utility_JsonToStructConverter;
import com.fico.platform.repository.v2alpha1.Asset;
import com.fico.platform.repository.v2alpha1.CreateAssetRequest;
import com.fico.platform.repository.v2alpha1.CreateAssetResponse;
import com.fico.platform.repository.v2alpha1.DeleteAssetRequest;
import com.fico.platform.repository.v2alpha1.DeleteAssetResponse;
import com.fico.platform.repository.v2alpha1.GetAssetResponse;
import com.fico.platform.repository.v2alpha1.ListAssetsRequest;
import com.fico.platform.repository.v2alpha1.ListAssetsResponse;
import com.fico.platform.repository.v2alpha1.StructuredContent;
import com.fico.platform.repository.v2alpha1.UpdateAssetRequest;
import com.fico.platform.repository.v2alpha1.UpdateAssetResponse;
import com.google.protobuf.FieldMask;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Struct;
import com.google.protobuf.util.FieldMaskUtil;
import com.google.protobuf.util.JsonFormat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class GrpcAssetServiceClientTest {

  private static final String SERVER_HOST = "assets.dms.dev.usw2.ficoanalyticcloud.com";
  private static final int SERVER_PORT = 443;
  private static final String ASSET_NAME =
      "assetTypes/com.fico.repo.qa/assets/b88d5313-1a36-4188-bec0-8f69af35a85b";
  public static final String PARENT = "assetTypes/com.fico.repo.qa";
  private static final String ASSET_TYPE_VERSION = "assetTypes/com.fico.repo.qa/versions/v1alpha1";

  private String authToken;
  private GrpcAssetServiceClient grpcAssetServiceClient;

  @Before
  public void setUp() throws IOException {
    // Arrange: Initialize authentication token and service client
    AuthTokenUtil authTokenUtil = new AuthTokenUtil();
    authToken = authTokenUtil.fetchToken();
    grpcAssetServiceClient = new GrpcAssetServiceClient(SERVER_HOST, SERVER_PORT);
  }

  @After
  public void tearDown() throws InterruptedException {
    // Shutdown the gRPC channel
    grpcAssetServiceClient.shutdown();
  }

  @Test
  public void test_GetAsset() {
    // Act
    GetAssetResponse response = grpcAssetServiceClient.getAsset(authToken, ASSET_NAME);

    // Assert
    assertTrue("Expected a non-null response for asset retrieval", response != null);
    // Add more assertions if needed
  }


  @Test
  public void test_CreateAsset() throws InvalidProtocolBufferException {

    // ****** Building CreateAssetRequest from scratch. StructuredContent from JSON file. *****
    // Arrange
    String jsonString = "{\"city\":\"Berlin\",\"country\":\"Germany\"}";
    Struct struct = Utility_JsonToStructConverter.convertJsonToStruct(jsonString);

    StructuredContent structuredContent = StructuredContent.newBuilder().setContent(struct).build();

    CreateAssetRequest createAssetRequest = CreateAssetRequest.newBuilder().setAsset(
            Asset.newBuilder().setDisplayName("Display Name").setDescription("Asset Desc")
                .setAssetTypeVersion(ASSET_TYPE_VERSION)
                .setStructuredContent(structuredContent))
        .setRequestId(String.valueOf(UUID.randomUUID()))
        .build();

    // Act
    CreateAssetResponse response =
        grpcAssetServiceClient.createAsset(authToken, createAssetRequest);

    System.out.println("Newly created asset: " + response.getAsset().getName());
    // Assert
    assertNotNull("Expected non-empty list of assets", response.getAsset().getName());
    // Add more assertions if needed
  }

  @Test
  public void test_CreateAsset_with_JSON_payload() throws IOException {

    // ****** Building CreateAssetRequest from JSON file. *****
    // Arrange

    CreateAssetRequest.Builder builder = CreateAssetRequest.newBuilder();
    builder.setRequestId(UUID.randomUUID().toString());
    JsonFormat.parser()
        .merge(new InputStreamReader(new ClassPathResource("createAsset.json").getInputStream()),
            builder);

    builder.setAsset(builder.getAsset().toBuilder().clearRemarkComment()
        .setRemarkComment("Remark kkk").build());

    CreateAssetRequest createAssetRequest = builder.build();

    // Act
    CreateAssetResponse response =
        grpcAssetServiceClient.createAsset(authToken, createAssetRequest);

    System.out.println("Newly created asset: " + response.getAsset().getName());
    // Assert
    assertNotNull("Expected non-empty list of assets", response.getAsset().getName());
    // Add more assertions if needed
  }


  @Test
  public void test_CreateAsset_with_StructuredContent_From_JSON_File() throws IOException {

    // ****** Building Structured Content from JSON file. *****
    // Arrange
    String jsonFileName = "Tree_with_1K_nodes.txt";
    String jsonString = getStringFromJsonFile(jsonFileName);
    Struct struct = Utility_JsonToStructConverter.convertJsonToStruct(jsonString);

    StructuredContent structuredContent = StructuredContent.newBuilder().setContent(struct).build();

    CreateAssetRequest createAssetRequest = CreateAssetRequest.newBuilder().setAsset(
            Asset.newBuilder().setDisplayName("Display Name").setDescription("Asset Desc")
                .setAssetTypeVersion(ASSET_TYPE_VERSION)
                .setStructuredContent(structuredContent))
        .setRequestId(String.valueOf(UUID.randomUUID()))
        .build();

    // Act
    CreateAssetResponse response =
        grpcAssetServiceClient.createAsset(authToken, createAssetRequest);

    System.out.println(
        "Newly created asset (SturcturedContent from file): " + response.getAsset().getName());
    // Assert
    assertNotNull("Expected non-empty list of assets", response.getAsset().getName());
    // Add more assertions if needed
  }


  @Test
  public void test_ListAssets() {
    // Arrange
    ListAssetsRequest listAssetsRequest = buildListAssetsRequest();

    // Act
    ListAssetsResponse response = grpcAssetServiceClient.listAssets(authToken, listAssetsRequest);

    // Assert
    assertTrue("Expected non-empty list of assets", response.getAssetsList().size() > 0);
    // Add more assertions if needed
  }

  @Test
  public void test_UpdateAsset() throws InvalidProtocolBufferException {

    // ****** Building CreateAssetRequest from scratch. StructuredContent from JSON file. *****

    //Create an Asset for Updating.
    CreateAssetResponse assetForUpdate = createAssetForTesting();

    String assetName = assetForUpdate.getAsset().getName();
    String etag = assetForUpdate.getAsset().getEtag();

    // Arrange
    String jsonString = "{\"city\":\"Berlin\",\"country\":\"Germany\"}";
    Struct struct = Utility_JsonToStructConverter.convertJsonToStruct(jsonString);
    StructuredContent structuredContent = StructuredContent.newBuilder().setContent(struct).build();

    String expectedDisplayName = "Update Display 7";
    Asset asset = Asset.newBuilder()
        .setName(assetName)
        .setDisplayName(expectedDisplayName)
        .setDescription("Update Description 7")
        .setRemarkComment("Updating remarks 7")
        .setStructuredContent(structuredContent)
        .build();

    UpdateAssetRequest updateAssetRequest = UpdateAssetRequest.newBuilder().setAsset(asset)
        .setRequestId(UUID.randomUUID().toString())
        .setUpdateMask(FieldMaskUtil.fromStringList(
            List.of("description", "display_name", "remark_comment", "structured_content")))
        .setEtag(etag)
        .build();

    // Act
    UpdateAssetResponse response =
        grpcAssetServiceClient.updateAsset(authToken, updateAssetRequest);

    System.out.println("Updated asset: " + response.getAsset().getName());
    // Assert
    assertNotNull("Expected non-empty structured_content of assets",
        response.getAsset().getStructuredContent());


    assertEquals("Expected non-empty structured_content of assets", expectedDisplayName,
        response.getAsset().getDisplayName());
    // Add more assertions if needed
  }

  @Test
  public void test_DeleteAsset() {
    CreateAssetResponse createAssetResponse = createAssetForTesting();
    String assetName = createAssetResponse.getAsset().getName();
    String etag = createAssetResponse.getAsset().getEtag();

    DeleteAssetRequest deleteAssetRequest = DeleteAssetRequest.newBuilder()
        .setRequestId(String.valueOf(UUID.randomUUID()))
        .setName(assetName)
        .setEtag(etag)
        .build();

    DeleteAssetResponse deleteAssetResponse =
        grpcAssetServiceClient.deleteAsset(authToken, deleteAssetRequest);
    assertNotNull("Expected  non-empty Delete Time ",
        deleteAssetResponse.getAsset().getDeleteTime());
  }

  //********* Helper methods *********
  public CreateAssetResponse createAssetForTesting() {
    //This asset will be used for Updating/Deleting
    Asset asset = Asset.newBuilder()
        .setDisplayName("New asset for testing")
        .setAssetTypeVersion(ASSET_TYPE_VERSION)
        .build();
    CreateAssetRequest createAssetRequest = CreateAssetRequest.newBuilder()
        .setAsset(asset)
        .setRequestId(String.valueOf(UUID.randomUUID()))
        .build();

    CreateAssetResponse createAssetResponse =
        grpcAssetServiceClient.createAsset(authToken, createAssetRequest);
    return createAssetResponse;
  }

  private ListAssetsRequest buildListAssetsRequest() {

    FieldMask fieldMask = FieldMask.newBuilder()
        .addAllPaths(
            Arrays.asList("name", "display_name", "description", "etag")) // Add more fields here
        .build();

    return ListAssetsRequest.newBuilder()
        .setParent(PARENT)
        .setFields(fieldMask)
        //.setFilter("description = \"ListCheck Mar11\"")
        //.setFilter("display_name = \"Asset_1712211532337\"")
        //.setOrderBy("display_name")
        .setPageSize(30)
        //.setPageToken("")
        .build();
  }


  public static String getStringFromJsonFile(String jsonFileName) {

    try {
      // Open an input stream for the file
      InputStream inputStream =
          ClasspathFileReader.class.getClassLoader().getResourceAsStream(jsonFileName);

      if (inputStream != null) {
        // Wrap the input stream in a BufferedReader to read lines
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        // Read lines from the file and append them to the StringBuilder
        while ((line = reader.readLine()) != null) {
          stringBuilder.append(line).append("\n");
        }

        String jsonString = stringBuilder.toString();
        // Close the reader and print the content of the file
        reader.close();
        // System.out.println("File content:");
        //System.out.println(stringBuilder.toString());

        return jsonString;
      } else {
        System.err.println("File not found: " + jsonFileName);
      }
    } catch (IOException e) {
      System.err.println("Error reading file: " + e.getMessage());
    }
    return null;
  }

}
