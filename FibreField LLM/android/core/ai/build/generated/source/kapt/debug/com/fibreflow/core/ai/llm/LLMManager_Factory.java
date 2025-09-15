package com.fibreflow.core.ai.llm;

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
public final class LLMManager_Factory implements Factory<LLMManager> {
  private final Provider<Context> contextProvider;

  public LLMManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public LLMManager get() {
    return newInstance(contextProvider.get());
  }

  public static LLMManager_Factory create(Provider<Context> contextProvider) {
    return new LLMManager_Factory(contextProvider);
  }

  public static LLMManager newInstance(Context context) {
    return new LLMManager(context);
  }
}
