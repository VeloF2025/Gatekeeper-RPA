package com.fibreflow.core.ai;

import com.fibreflow.core.ai.vision.BarcodeScanner;
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
public final class AIModule_ProvideBarcodeScannerFactory implements Factory<BarcodeScanner> {
  @Override
  public BarcodeScanner get() {
    return provideBarcodeScanner();
  }

  public static AIModule_ProvideBarcodeScannerFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static BarcodeScanner provideBarcodeScanner() {
    return Preconditions.checkNotNullFromProvides(AIModule.INSTANCE.provideBarcodeScanner());
  }

  private static final class InstanceHolder {
    private static final AIModule_ProvideBarcodeScannerFactory INSTANCE = new AIModule_ProvideBarcodeScannerFactory();
  }
}
