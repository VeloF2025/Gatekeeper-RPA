package com.fibreflow.core.ai.vision;

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
public final class PhotoQualityAnalyzer_Factory implements Factory<PhotoQualityAnalyzer> {
  @Override
  public PhotoQualityAnalyzer get() {
    return newInstance();
  }

  public static PhotoQualityAnalyzer_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static PhotoQualityAnalyzer newInstance() {
    return new PhotoQualityAnalyzer();
  }

  private static final class InstanceHolder {
    private static final PhotoQualityAnalyzer_Factory INSTANCE = new PhotoQualityAnalyzer_Factory();
  }
}
