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
public final class BarcodeScanner_Factory implements Factory<BarcodeScanner> {
  @Override
  public BarcodeScanner get() {
    return newInstance();
  }

  public static BarcodeScanner_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static BarcodeScanner newInstance() {
    return new BarcodeScanner();
  }

  private static final class InstanceHolder {
    private static final BarcodeScanner_Factory INSTANCE = new BarcodeScanner_Factory();
  }
}
