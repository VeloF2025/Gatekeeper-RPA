package com.fibreflow.core.ai.pipeline;

import com.fibreflow.core.ai.inference.InferenceEngine;
import com.fibreflow.core.ai.llm.LLMManager;
import com.fibreflow.core.ai.vision.BarcodeScanner;
import com.fibreflow.core.ai.vision.ONTLightDetector;
import com.fibreflow.core.ai.vision.PhotoQualityAnalyzer;
import com.fibreflow.core.ai.vision.TextExtractor;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("dagger.hilt.android.scopes.ViewModelScoped")
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
public final class ModelCoordinator_Factory implements Factory<ModelCoordinator> {
  private final Provider<InferenceEngine> inferenceEngineProvider;

  private final Provider<LLMManager> llmManagerProvider;

  private final Provider<PhotoQualityAnalyzer> photoQualityAnalyzerProvider;

  private final Provider<BarcodeScanner> barcodeScannerProvider;

  private final Provider<TextExtractor> textExtractorProvider;

  private final Provider<ONTLightDetector> ontLightDetectorProvider;

  public ModelCoordinator_Factory(Provider<InferenceEngine> inferenceEngineProvider,
      Provider<LLMManager> llmManagerProvider,
      Provider<PhotoQualityAnalyzer> photoQualityAnalyzerProvider,
      Provider<BarcodeScanner> barcodeScannerProvider,
      Provider<TextExtractor> textExtractorProvider,
      Provider<ONTLightDetector> ontLightDetectorProvider) {
    this.inferenceEngineProvider = inferenceEngineProvider;
    this.llmManagerProvider = llmManagerProvider;
    this.photoQualityAnalyzerProvider = photoQualityAnalyzerProvider;
    this.barcodeScannerProvider = barcodeScannerProvider;
    this.textExtractorProvider = textExtractorProvider;
    this.ontLightDetectorProvider = ontLightDetectorProvider;
  }

  @Override
  public ModelCoordinator get() {
    return newInstance(inferenceEngineProvider.get(), llmManagerProvider.get(), photoQualityAnalyzerProvider.get(), barcodeScannerProvider.get(), textExtractorProvider.get(), ontLightDetectorProvider.get());
  }

  public static ModelCoordinator_Factory create(Provider<InferenceEngine> inferenceEngineProvider,
      Provider<LLMManager> llmManagerProvider,
      Provider<PhotoQualityAnalyzer> photoQualityAnalyzerProvider,
      Provider<BarcodeScanner> barcodeScannerProvider,
      Provider<TextExtractor> textExtractorProvider,
      Provider<ONTLightDetector> ontLightDetectorProvider) {
    return new ModelCoordinator_Factory(inferenceEngineProvider, llmManagerProvider, photoQualityAnalyzerProvider, barcodeScannerProvider, textExtractorProvider, ontLightDetectorProvider);
  }

  public static ModelCoordinator newInstance(InferenceEngine inferenceEngine, LLMManager llmManager,
      PhotoQualityAnalyzer photoQualityAnalyzer, BarcodeScanner barcodeScanner,
      TextExtractor textExtractor, ONTLightDetector ontLightDetector) {
    return new ModelCoordinator(inferenceEngine, llmManager, photoQualityAnalyzer, barcodeScanner, textExtractor, ontLightDetector);
  }
}
