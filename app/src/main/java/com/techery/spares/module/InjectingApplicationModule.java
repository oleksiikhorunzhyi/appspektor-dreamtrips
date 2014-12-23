package com.techery.spares.module;

import android.content.Context;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.application.BaseApplicationWithInjector;
import com.techery.spares.module.Annotations.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

@Module(
        includes = {
                AndroidServicesModule.class,
                EventBusModule.class,
                AndroidServicesModule.class,
                ConcurentModule.class,
                SupportModule.class
        },
        library = true,
        complete = false
)
public class InjectingApplicationModule {

    @Provides
    @Singleton
    Context provideContext(BaseApplicationWithInjector baseApplicationWithInjector) {
        return baseApplicationWithInjector.getApplicationContext();
    }

    @Provides
    @Singleton
    @Application
    ObjectGraph provideObjectGraph(BaseApplicationWithInjector baseApplicationWithInjector) {
        return baseApplicationWithInjector.getObjectGraph();
    }

    @Provides
    @Singleton
    @Application
    Injector provideInjector(BaseApplicationWithInjector baseApplicationWithInjector) {
        return baseApplicationWithInjector;
    }

    @Provides(type = Provides.Type.SET)
    AppInitializer provideEmptyInitializer() {
        return new AppInitializer() {
            @Override
            public void initialize() {

            }
        };
    }
}
