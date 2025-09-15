package com.fibreflow.core.ai.pipeline;

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
public final class ValidationPipeline_Factory implements Factory<ValidationPipeline> {
  private final Provider<ModelCoordinator> modelCoordinatorProvider;

  public ValidationPipeline_Factory(Provider<ModelCoordinator> modelCoordinatorProvider) {
    this.modelCoordinatorProvider = modelCoordinatorProvider;
  }

  @Override
  public ValidationPipeline get() {
    return newInstance(modelCoordinatorProvider.get());
  }

  public static ValidationPipeline_Factory create(
      Provider<ModelCoordinator> modelCoordinatorProvider) {
    return new ValidationPipeline_Factory(modelCoordinatorProvider);
  }

  public static ValidationPipeline newInstance(ModelCoordinator modelCoordinator) {
    return new ValidationPipeline(modelCoordinator);
  }
}
