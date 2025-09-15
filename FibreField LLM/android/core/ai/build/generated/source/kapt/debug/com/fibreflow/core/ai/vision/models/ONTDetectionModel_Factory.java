package com.fibreflow.core.ai.vision.models;

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
public final class ONTDetectionModel_Factory implements Factory<ONTDetectionModel> {
  private final Provider<Context> contextProvider;

  public ONTDetectionModel_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public ONTDetectionModel get() {
    return newInstance(contextProvider.get());
  }

  public static ONTDetectionModel_Factory create(Provider<Context> contextProvider) {
    return new ONTDetectionModel_Factory(contextProvider);
  }

  public static ONTDetectionModel newInstance(Context context) {
    return new ONTDetectionModel(context);
  }
}
