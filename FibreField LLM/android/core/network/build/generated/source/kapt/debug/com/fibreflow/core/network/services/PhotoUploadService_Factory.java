package com.fibreflow.core.network.services;

import com.fibreflow.core.network.api.InstallationAPI;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class PhotoUploadService_Factory implements Factory<PhotoUploadService> {
  private final Provider<InstallationAPI> installationApiProvider;

  public PhotoUploadService_Factory(Provider<InstallationAPI> installationApiProvider) {
    this.installationApiProvider = installationApiProvider;
  }

  @Override
  public PhotoUploadService get() {
    return newInstance(installationApiProvider.get());
  }

  public static PhotoUploadService_Factory create(
      Provider<InstallationAPI> installationApiProvider) {
    return new PhotoUploadService_Factory(installationApiProvider);
  }

  public static PhotoUploadService newInstance(InstallationAPI installationApi) {
    return new PhotoUploadService(installationApi);
  }
}
