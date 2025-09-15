package com.fibreflow.core.ai;

import com.fibreflow.core.ai.vision.TextExtractor;
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
public final class AIModule_ProvideTextExtractorFactory implements Factory<TextExtractor> {
  @Override
  public TextExtractor get() {
    return provideTextExtractor();
  }

  public static AIModule_ProvideTextExtractorFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static TextExtractor provideTextExtractor() {
    return Preconditions.checkNotNullFromProvides(AIModule.INSTANCE.provideTextExtractor());
  }

  private static final class InstanceHolder {
    private static final AIModule_ProvideTextExtractorFactory INSTANCE = new AIModule_ProvideTextExtractorFactory();
  }
}
