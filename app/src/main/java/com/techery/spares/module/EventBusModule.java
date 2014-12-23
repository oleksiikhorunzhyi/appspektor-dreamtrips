package com.techery.spares.module;

import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.Annotations.Private;

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
