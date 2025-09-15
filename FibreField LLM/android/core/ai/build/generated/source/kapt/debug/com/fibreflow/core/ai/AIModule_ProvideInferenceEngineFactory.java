package com.fibreflow.core.ai;

import android.content.Context;
import com.fibreflow.core.ai.inference.InferenceEngine;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AIModule_ProvideInferenceEngineFactory implements Factory<InferenceEngine> {
  private final Provider<Context> contextProvider;

  public AIModule_ProvideInferenceEngineFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public InferenceEngine get() {
    return provideInferenceEngine(contextProvider.get());
  }

  public static AIModule_ProvideInferenceEngineFactory create(Provider<Context> contextProvider) {
    return new AIModule_ProvideInferenceEngineFactory(contextProvider);
  }

  public static InferenceEngine provideInferenceEngine(Context context) {
    return Preconditions.checkNotNullFromProvides(AIModule.INSTANCE.provideInferenceEngine(context));
  }
}
