package com.worldventures.dreamtrips.core.initializer;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
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
