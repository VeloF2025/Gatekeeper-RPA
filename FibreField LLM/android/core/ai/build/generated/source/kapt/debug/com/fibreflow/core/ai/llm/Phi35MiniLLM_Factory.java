package com.fibreflow.core.ai.llm;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class Phi35MiniLLM_Factory implements Factory<Phi35MiniLLM> {
  private final Provider<Context> contextProvider;

  public Phi35MiniLLM_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public Phi35MiniLLM get() {
    return newInstance(contextProvider.get());
  }

  public static Phi35MiniLLM_Factory create(Provider<Context> contextProvider) {
    return new Phi35MiniLLM_Factory(contextProvider);
  }

  public static Phi35MiniLLM newInstance(Context context) {
    return new Phi35MiniLLM(context);
  }
}
