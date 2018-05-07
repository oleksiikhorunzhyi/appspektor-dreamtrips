package com.techery.spares.module;

import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.App;

import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

@Module(
      includes = {
            // base helpers and drivers
            StorageModule.class},
      library = true,
      complete = false)
public class InjectingApplicationModule {

   @Provides
   @Singleton
   @ForApplication
   ObjectGraph provideObjectGraph(App app) {
      return app.getObjectGraph();
   }

   @Provides
   @Singleton
   @ForApplication
   Injector provideInjector(App app) {
      return app;
   }

}
