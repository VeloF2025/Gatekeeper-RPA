package com.fibreflow.core.ai.feedback;

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
public final class FeedbackCollector_Factory implements Factory<FeedbackCollector> {
  @Override
  public FeedbackCollector get() {
    return newInstance();
  }

  public static FeedbackCollector_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static FeedbackCollector newInstance() {
    return new FeedbackCollector();
  }

  private static final class InstanceHolder {
    private static final FeedbackCollector_Factory INSTANCE = new FeedbackCollector_Factory();
  }
}
