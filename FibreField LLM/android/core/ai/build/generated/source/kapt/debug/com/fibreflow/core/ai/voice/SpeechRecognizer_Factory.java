package com.fibreflow.core.ai.voice;

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
public final class SpeechRecognizer_Factory implements Factory<SpeechRecognizer> {
  private final Provider<Context> contextProvider;

  public SpeechRecognizer_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SpeechRecognizer get() {
    return newInstance(contextProvider.get());
  }

  public static SpeechRecognizer_Factory create(Provider<Context> contextProvider) {
    return new SpeechRecognizer_Factory(contextProvider);
  }

  public static SpeechRecognizer newInstance(Context context) {
    return new SpeechRecognizer(context);
  }
}
