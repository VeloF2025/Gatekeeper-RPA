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
public final class PowerProfileManager_Factory implements Factory<PowerProfileManager> {
  private final Provider<Context> contextProvider;

  private final Provider<BatteryManager> batteryManagerProvider;

  public PowerProfileManager_Factory(Provider<Context> contextProvider,
      Provider<BatteryManager> batteryManagerProvider) {
    this.contextProvider = contextProvider;
    this.batteryManagerProvider = batteryManagerProvider;
  }

  @Override
  public PowerProfileManager get() {
    return newInstance(contextProvider.get(), batteryManagerProvider.get());
  }

  public static PowerProfileManager_Factory create(Provider<Context> contextProvider,
      Provider<BatteryManager> batteryManagerProvider) {
    return new PowerProfileManager_Factory(contextProvider, batteryManagerProvider);
  }

  public static PowerProfileManager newInstance(Context context, BatteryManager batteryManager) {
    return new PowerProfileManager(context, batteryManager);
  }
}
