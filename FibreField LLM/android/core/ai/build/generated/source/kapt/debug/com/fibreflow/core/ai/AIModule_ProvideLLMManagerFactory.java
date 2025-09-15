package com.fibreflow.core.ai;

import android.content.Context;
import com.fibreflow.core.ai.llm.LLMManager;
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
public final class AIModule_ProvideLLMManagerFactory implements Factory<LLMManager> {
  private final Provider<Context> contextProvider;

  public AIModule_ProvideLLMManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public LLMManager get() {
    return provideLLMManager(contextProvider.get());
  }

  public static AIModule_ProvideLLMManagerFactory create(Provider<Context> contextProvider) {
    return new AIModule_ProvideLLMManagerFactory(contextProvider);
  }

  public static LLMManager provideLLMManager(Context context) {
    return Preconditions.checkNotNullFromProvides(AIModule.INSTANCE.provideLLMManager(context));
  }
}
