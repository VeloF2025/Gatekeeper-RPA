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
public final class TextToSpeech_Factory implements Factory<TextToSpeech> {
  private final Provider<Context> contextProvider;

  public TextToSpeech_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public TextToSpeech get() {
    return newInstance(contextProvider.get());
  }

  public static TextToSpeech_Factory create(Provider<Context> contextProvider) {
    return new TextToSpeech_Factory(contextProvider);
  }

  public static TextToSpeech newInstance(Context context) {
    return new TextToSpeech(context);
  }
}
