package com.fibreflow.core.ai;

import com.fibreflow.core.ai.vision.PhotoQualityAnalyzer;
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
public final class AIModule_ProvidePhotoQualityAnalyzerFactory implements Factory<PhotoQualityAnalyzer> {
  @Override
  public PhotoQualityAnalyzer get() {
    return providePhotoQualityAnalyzer();
  }

  public static AIModule_ProvidePhotoQualityAnalyzerFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static PhotoQualityAnalyzer providePhotoQualityAnalyzer() {
    return Preconditions.checkNotNullFromProvides(AIModule.INSTANCE.providePhotoQualityAnalyzer());
  }

  private static final class InstanceHolder {
    private static final AIModule_ProvidePhotoQualityAnalyzerFactory INSTANCE = new AIModule_ProvidePhotoQualityAnalyzerFactory();
  }
}
