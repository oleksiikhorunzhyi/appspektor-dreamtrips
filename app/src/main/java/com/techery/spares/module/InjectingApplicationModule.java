package com.techery.spares.module;

import android.content.Context;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.application.BaseApplicationWithInjector;
import com.techery.spares.module.Annotations.Application;
import com.techery.spares.service.ServiceActionRunner;

import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

@Module(
        includes = {
                EventBusModule.class,
                AndroidServicesModule.class,
                ConcurentModule.class,
                SupportModule.class,
                StorageModule.class
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
            public void initialize(Injector injector) {

            }
        };
    }

    @Provides
    ServiceActionRunner provideServiceActionRunner(Context context) {
        return new ServiceActionRunner(context);
    }
}
