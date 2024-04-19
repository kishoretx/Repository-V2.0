package com.example.repov2.security;

import io.grpc.CallCredentials;
import io.grpc.Metadata;
import io.grpc.Status;
import java.util.concurrent.Executor;

public class CustomTokenCallCredential extends CallCredentials {
  private final String authToken;

  public CustomTokenCallCredential(String authToken) {
    this.authToken = authToken;
  }


  @Override
  public void applyRequestMetadata(RequestInfo requestInfo, Executor executor,
                                   MetadataApplier metadataApplier) {
    executor.execute(() -> {
      try {
        Metadata metadata = new Metadata();
        metadata.put(Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER),
            "Bearer " + authToken);
        metadataApplier.apply(metadata);
      } catch (Throwable e) {
        metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
      }
    });
  }

  @Override
  public void thisUsesUnstableApi() {
    // no-op
  }
}
