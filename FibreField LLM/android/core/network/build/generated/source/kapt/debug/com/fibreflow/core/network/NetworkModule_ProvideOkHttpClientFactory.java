package com.fibreflow.core.network;

import com.fibreflow.core.network.interceptors.AuthInterceptor;
import com.fibreflow.core.network.interceptors.LoggingInterceptor;
import com.fibreflow.core.network.interceptors.NetworkSecurityInterceptor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;

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
public final class NetworkModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<AuthInterceptor> authInterceptorProvider;

  private final Provider<LoggingInterceptor> loggingInterceptorProvider;

  private final Provider<NetworkSecurityInterceptor> securityInterceptorProvider;

  private final Provider<CertificatePinner> certificatePinnerProvider;

  public NetworkModule_ProvideOkHttpClientFactory(Provider<AuthInterceptor> authInterceptorProvider,
      Provider<LoggingInterceptor> loggingInterceptorProvider,
      Provider<NetworkSecurityInterceptor> securityInterceptorProvider,
      Provider<CertificatePinner> certificatePinnerProvider) {
    this.authInterceptorProvider = authInterceptorProvider;
    this.loggingInterceptorProvider = loggingInterceptorProvider;
    this.securityInterceptorProvider = securityInterceptorProvider;
    this.certificatePinnerProvider = certificatePinnerProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(authInterceptorProvider.get(), loggingInterceptorProvider.get(), securityInterceptorProvider.get(), certificatePinnerProvider.get());
  }

  public static NetworkModule_ProvideOkHttpClientFactory create(
      Provider<AuthInterceptor> authInterceptorProvider,
      Provider<LoggingInterceptor> loggingInterceptorProvider,
      Provider<NetworkSecurityInterceptor> securityInterceptorProvider,
      Provider<CertificatePinner> certificatePinnerProvider) {
    return new NetworkModule_ProvideOkHttpClientFactory(authInterceptorProvider, loggingInterceptorProvider, securityInterceptorProvider, certificatePinnerProvider);
  }

  public static OkHttpClient provideOkHttpClient(AuthInterceptor authInterceptor,
      LoggingInterceptor loggingInterceptor, NetworkSecurityInterceptor securityInterceptor,
      CertificatePinner certificatePinner) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttpClient(authInterceptor, loggingInterceptor, securityInterceptor, certificatePinner));
  }
}
