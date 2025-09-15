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
public final class ONTLightDetector_Factory implements Factory<ONTLightDetector> {
  @Override
  public ONTLightDetector get() {
    return newInstance();
  }

  public static ONTLightDetector_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ONTLightDetector newInstance() {
    return new ONTLightDetector();
  }

  private static final class InstanceHolder {
    private static final ONTLightDetector_Factory INSTANCE = new ONTLightDetector_Factory();
  }
}
