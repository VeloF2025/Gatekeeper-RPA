package com.fibreflow.core.ai;

import android.content.Context;
import com.fibreflow.core.ai.voice.SpeechRecognizer;
import com.fibreflow.core.ai.voice.VoiceCommandProcessor;
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
public final class AIModule_ProvideSpeechRecognizerFactory implements Factory<SpeechRecognizer> {
  private final Provider<Context> contextProvider;

  private final Provider<VoiceCommandProcessor> voiceCommandProcessorProvider;

  public AIModule_ProvideSpeechRecognizerFactory(Provider<Context> contextProvider,
      Provider<VoiceCommandProcessor> voiceCommandProcessorProvider) {
    this.contextProvider = contextProvider;
    this.voiceCommandProcessorProvider = voiceCommandProcessorProvider;
  }

  @Override
  public SpeechRecognizer get() {
    return provideSpeechRecognizer(contextProvider.get(), voiceCommandProcessorProvider.get());
  }

  public static AIModule_ProvideSpeechRecognizerFactory create(Provider<Context> contextProvider,
      Provider<VoiceCommandProcessor> voiceCommandProcessorProvider) {
    return new AIModule_ProvideSpeechRecognizerFactory(contextProvider, voiceCommandProcessorProvider);
  }

  public static SpeechRecognizer provideSpeechRecognizer(Context context,
      VoiceCommandProcessor voiceCommandProcessor) {
    return Preconditions.checkNotNullFromProvides(AIModule.INSTANCE.provideSpeechRecognizer(context, voiceCommandProcessor));
  }
}
