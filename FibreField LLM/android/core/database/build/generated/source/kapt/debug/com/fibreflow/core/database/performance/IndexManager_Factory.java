package com.fibreflow.core.database.performance;

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
public final class IndexManager_Factory implements Factory<IndexManager> {
  @Override
  public IndexManager get() {
    return newInstance();
  }

  public static IndexManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static IndexManager newInstance() {
    return new IndexManager();
  }

  private static final class InstanceHolder {
    private static final IndexManager_Factory INSTANCE = new IndexManager_Factory();
  }
}
