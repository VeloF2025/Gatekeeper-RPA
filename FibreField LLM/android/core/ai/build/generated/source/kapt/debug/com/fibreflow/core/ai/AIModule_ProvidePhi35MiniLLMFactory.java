package com.fibreflow.core.ai;

import android.content.Context;
import com.fibreflow.core.ai.llm.Phi35MiniLLM;
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
public final class AIModule_ProvidePhi35MiniLLMFactory implements Factory<Phi35MiniLLM> {
  private final Provider<Context> contextProvider;

  public AIModule_ProvidePhi35MiniLLMFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public Phi35MiniLLM get() {
    return providePhi35MiniLLM(contextProvider.get());
  }

  public static AIModule_ProvidePhi35MiniLLMFactory create(Provider<Context> contextProvider) {
    return new AIModule_ProvidePhi35MiniLLMFactory(contextProvider);
  }

  public static Phi35MiniLLM providePhi35MiniLLM(Context context) {
    return Preconditions.checkNotNullFromProvides(AIModule.INSTANCE.providePhi35MiniLLM(context));
  }
}
