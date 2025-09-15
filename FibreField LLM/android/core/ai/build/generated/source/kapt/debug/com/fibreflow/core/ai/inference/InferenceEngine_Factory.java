package com.fibreflow.core.ai.inference;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class InferenceEngine_Factory implements Factory<InferenceEngine> {
  private final Provider<Context> contextProvider;

  public InferenceEngine_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public InferenceEngine get() {
    return newInstance(contextProvider.get());
  }

  public static InferenceEngine_Factory create(Provider<Context> contextProvider) {
    return new InferenceEngine_Factory(contextProvider);
  }

  public static InferenceEngine newInstance(Context context) {
    return new InferenceEngine(context);
  }
}
