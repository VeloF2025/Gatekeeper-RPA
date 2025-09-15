package com.fibreflow.core.ai;

import com.fibreflow.core.ai.voice.VoiceCommandProcessor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class AIModule_ProvideVoiceCommandProcessorFactory implements Factory<VoiceCommandProcessor> {
  @Override
  public VoiceCommandProcessor get() {
    return provideVoiceCommandProcessor();
  }

  public static AIModule_ProvideVoiceCommandProcessorFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static VoiceCommandProcessor provideVoiceCommandProcessor() {
    return Preconditions.checkNotNullFromProvides(AIModule.INSTANCE.provideVoiceCommandProcessor());
  }

  private static final class InstanceHolder {
    private static final AIModule_ProvideVoiceCommandProcessorFactory INSTANCE = new AIModule_ProvideVoiceCommandProcessorFactory();
  }
}
