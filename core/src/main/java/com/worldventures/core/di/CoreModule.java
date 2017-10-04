package com.worldventures.core.di;

import com.worldventures.core.modules.ServiceModule;
import com.worldventures.core.modules.auth.AuthModule;
import com.worldventures.core.modules.facebook.FacebookAppModule;
import com.worldventures.core.modules.infopages.SupportModule;
import com.worldventures.core.modules.picker.MediaPickerAppModule;
import com.worldventures.core.modules.settings.SettingsModule;
import com.worldventures.core.modules.video.MediaModule;

import dagger.Module;

@Module(
      library = true,
      complete = false,
      injects = {
            BaseApplicationWithInjector.class
      },
      includes = {
            ServiceModule.class,
            AuthModule.class,
            SupportModule.class,
            FacebookAppModule.class,
            MediaModule.class,
            SettingsModule.class,
            MediaPickerAppModule.class,
      }
)
public class CoreModule {
}
