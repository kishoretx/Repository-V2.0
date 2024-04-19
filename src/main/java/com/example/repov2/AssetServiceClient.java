package com.example.repov2;

import com.fico.platform.repository.v2alpha1.CreateAssetRequest;
import com.fico.platform.repository.v2alpha1.CreateAssetResponse;
import com.fico.platform.repository.v2alpha1.DeleteAssetRequest;
import com.fico.platform.repository.v2alpha1.DeleteAssetResponse;
import com.fico.platform.repository.v2alpha1.GetAssetResponse;
import com.fico.platform.repository.v2alpha1.ListAssetTypesResponse;
import com.fico.platform.repository.v2alpha1.ListAssetsRequest;
import com.fico.platform.repository.v2alpha1.ListAssetsResponse;
import com.fico.platform.repository.v2alpha1.UpdateAssetRequest;
import com.fico.platform.repository.v2alpha1.UpdateAssetResponse;

public interface AssetServiceClient {
    CreateAssetResponse createAsset(String authToken, CreateAssetRequest createAssetRequest);
    GetAssetResponse getAsset(String authToken, String assetName);
    UpdateAssetResponse updateAsset(String authToken, UpdateAssetRequest updateAssetRequest);
    DeleteAssetResponse deleteAsset(String authToken, DeleteAssetRequest deleteAssetRequest);
    ListAssetTypesResponse listAssetTypes(String authToken, int pageSize);
    ListAssetTypesResponse listAssetTypes(String authToken, int pageSize, String pageToken);
    ListAssetsResponse listAssets(String authToken, ListAssetsRequest listAssetsRequest);
    void shutdown() throws InterruptedException;
}
