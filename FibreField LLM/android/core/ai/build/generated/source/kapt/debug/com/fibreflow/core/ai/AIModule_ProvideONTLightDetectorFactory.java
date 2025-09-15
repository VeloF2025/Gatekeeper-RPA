package com.fibreflow.core.ai;

import com.fibreflow.core.ai.vision.ONTLightDetector;
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
public final class AIModule_ProvideONTLightDetectorFactory implements Factory<ONTLightDetector> {
  @Override
  public ONTLightDetector get() {
    return provideONTLightDetector();
  }

  public static AIModule_ProvideONTLightDetectorFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ONTLightDetector provideONTLightDetector() {
    return Preconditions.checkNotNullFromProvides(AIModule.INSTANCE.provideONTLightDetector());
  }

  private static final class InstanceHolder {
    private static final AIModule_ProvideONTLightDetectorFactory INSTANCE = new AIModule_ProvideONTLightDetectorFactory();
  }
}
