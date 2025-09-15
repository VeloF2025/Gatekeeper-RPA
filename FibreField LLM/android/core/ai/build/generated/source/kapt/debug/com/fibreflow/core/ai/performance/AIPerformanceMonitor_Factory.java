package com.fibreflow.core.ai.performance;

import android.content.Context;
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
public final class AIPerformanceMonitor_Factory implements Factory<AIPerformanceMonitor> {
  private final Provider<Context> contextProvider;

  public AIPerformanceMonitor_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AIPerformanceMonitor get() {
    return newInstance(contextProvider.get());
  }

  public static AIPerformanceMonitor_Factory create(Provider<Context> contextProvider) {
    return new AIPerformanceMonitor_Factory(contextProvider);
  }

  public static AIPerformanceMonitor newInstance(Context context) {
    return new AIPerformanceMonitor(context);
  }
}
