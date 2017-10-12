package com.techery.spares.module;

import com.worldventures.core.di.qualifier.Global;
import com.worldventures.core.di.qualifier.Private;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module(library = true)
public class EventBusModule {
   @Provides
   @Global
   @Singleton
   EventBus provideGlobalEventBus() {
      return EventBus.getDefault();
   }

   @Provides
   @Private
   EventBus provideEventBus() {
      return new EventBus();
   }
}
