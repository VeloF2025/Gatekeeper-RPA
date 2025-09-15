package com.fibreflow.core.network;

import com.fibreflow.core.network.interceptors.LoggingInterceptor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class NetworkModule_ProvideLoggingInterceptorFactory implements Factory<LoggingInterceptor> {
  @Override
  public LoggingInterceptor get() {
    return provideLoggingInterceptor();
  }

  public static NetworkModule_ProvideLoggingInterceptorFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static LoggingInterceptor provideLoggingInterceptor() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideLoggingInterceptor());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideLoggingInterceptorFactory INSTANCE = new NetworkModule_ProvideLoggingInterceptorFactory();
  }
}
