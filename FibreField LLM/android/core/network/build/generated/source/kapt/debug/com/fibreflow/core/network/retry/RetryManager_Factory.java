package com.fibreflow.core.network.retry;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class RetryManager_Factory implements Factory<RetryManager> {
  @Override
  public RetryManager get() {
    return newInstance();
  }

  public static RetryManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RetryManager newInstance() {
    return new RetryManager();
  }

  private static final class InstanceHolder {
    private static final RetryManager_Factory INSTANCE = new RetryManager_Factory();
  }
}
