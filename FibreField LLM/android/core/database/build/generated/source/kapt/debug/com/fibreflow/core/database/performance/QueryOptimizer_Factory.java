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
public final class QueryOptimizer_Factory implements Factory<QueryOptimizer> {
  @Override
  public QueryOptimizer get() {
    return newInstance();
  }

  public static QueryOptimizer_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static QueryOptimizer newInstance() {
    return new QueryOptimizer();
  }

  private static final class InstanceHolder {
    private static final QueryOptimizer_Factory INSTANCE = new QueryOptimizer_Factory();
  }
}
