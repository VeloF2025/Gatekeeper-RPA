package com.fibreflow.core.network;

import android.content.Context;
import com.fibreflow.core.network.interceptors.NetworkSecurityInterceptor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class NetworkModule_ProvideNetworkSecurityInterceptorFactory implements Factory<NetworkSecurityInterceptor> {
  private final Provider<Context> contextProvider;

  public NetworkModule_ProvideNetworkSecurityInterceptorFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public NetworkSecurityInterceptor get() {
    return provideNetworkSecurityInterceptor(contextProvider.get());
  }

  public static NetworkModule_ProvideNetworkSecurityInterceptorFactory create(
      Provider<Context> contextProvider) {
    return new NetworkModule_ProvideNetworkSecurityInterceptorFactory(contextProvider);
  }

  public static NetworkSecurityInterceptor provideNetworkSecurityInterceptor(Context context) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideNetworkSecurityInterceptor(context));
  }
}
