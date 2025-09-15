package com.fibreflow.core.ai.voice;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class VoiceCommandProcessor_Factory implements Factory<VoiceCommandProcessor> {
  @Override
  public VoiceCommandProcessor get() {
    return newInstance();
  }

  public static VoiceCommandProcessor_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static VoiceCommandProcessor newInstance() {
    return new VoiceCommandProcessor();
  }

  private static final class InstanceHolder {
    private static final VoiceCommandProcessor_Factory INSTANCE = new VoiceCommandProcessor_Factory();
  }
}
