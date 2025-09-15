package com.fibreflow.core.network.services;

import com.fibreflow.core.network.api.InstallationAPI;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import error.NonExistentClass;
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

  private final Provider<NonExistentClass> offlineQueueProvider;

  public PhotoUploadService_Factory(Provider<InstallationAPI> installationApiProvider,
      Provider<NonExistentClass> offlineQueueProvider) {
    this.installationApiProvider = installationApiProvider;
    this.offlineQueueProvider = offlineQueueProvider;
  }

  @Override
  public PhotoUploadService get() {
    return newInstance(installationApiProvider.get(), offlineQueueProvider.get());
  }

  public static PhotoUploadService_Factory create(Provider<InstallationAPI> installationApiProvider,
      Provider<NonExistentClass> offlineQueueProvider) {
    return new PhotoUploadService_Factory(installationApiProvider, offlineQueueProvider);
  }

  public static PhotoUploadService newInstance(InstallationAPI installationApi,
      NonExistentClass offlineQueue) {
    return new PhotoUploadService(installationApi, offlineQueue);
  }
}
