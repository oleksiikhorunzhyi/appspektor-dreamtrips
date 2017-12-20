package com.worldventures.dreamtrips.core.initializer;

import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor;
import com.worldventures.dreamtrips.modules.config.service.command.LoadConfigurationCommand;

public class AppConfigurationInitializer implements AppInitializer {

   private final AppConfigurationInteractor appConfigurationInteractor;

   public AppConfigurationInitializer(AppConfigurationInteractor appConfigurationInteractor) {
      this.appConfigurationInteractor = appConfigurationInteractor;
   }

   @Override
   public void initialize(Injector injector) {
      appConfigurationInteractor.getLoadConfigPipe().send(new LoadConfigurationCommand());
   }
}
