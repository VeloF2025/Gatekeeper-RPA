package com.fibreflow.core.ai.power;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class BatteryManager_Factory implements Factory<BatteryManager> {
  private final Provider<Context> contextProvider;

  public BatteryManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BatteryManager get() {
    return newInstance(contextProvider.get());
  }

  public static BatteryManager_Factory create(Provider<Context> contextProvider) {
    return new BatteryManager_Factory(contextProvider);
  }

  public static BatteryManager newInstance(Context context) {
    return new BatteryManager(context);
  }
}
