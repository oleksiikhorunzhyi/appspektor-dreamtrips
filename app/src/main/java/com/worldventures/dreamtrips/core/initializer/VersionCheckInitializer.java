package com.worldventures.dreamtrips.core.initializer;

import com.worldventures.core.di.AppInitializer;
import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor;
import com.worldventures.dreamtrips.modules.config.service.command.LoadConfigurationCommand;

public class VersionCheckInitializer implements AppInitializer {
   private AppConfigurationInteractor appConfigurationInteractor;

   public VersionCheckInitializer(AppConfigurationInteractor appConfigurationInteractor) {
      this.appConfigurationInteractor = appConfigurationInteractor;
   }

   @Override
   public void initialize(Injector injector) {
      appConfigurationInteractor.loadConfigurationPipe().send(new LoadConfigurationCommand());
   }
}
