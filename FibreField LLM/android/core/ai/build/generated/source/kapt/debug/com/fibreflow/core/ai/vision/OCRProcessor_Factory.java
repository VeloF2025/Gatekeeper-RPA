package com.fibreflow.core.ai.vision;

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
public final class OCRProcessor_Factory implements Factory<OCRProcessor> {
  private final Provider<TextExtractor> textExtractorProvider;

  public OCRProcessor_Factory(Provider<TextExtractor> textExtractorProvider) {
    this.textExtractorProvider = textExtractorProvider;
  }

  @Override
  public OCRProcessor get() {
    return newInstance(textExtractorProvider.get());
  }

  public static OCRProcessor_Factory create(Provider<TextExtractor> textExtractorProvider) {
    return new OCRProcessor_Factory(textExtractorProvider);
  }

  public static OCRProcessor newInstance(TextExtractor textExtractor) {
    return new OCRProcessor(textExtractor);
  }
}
