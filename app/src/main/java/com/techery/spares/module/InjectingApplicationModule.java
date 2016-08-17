package com.techery.spares.module;

import android.app.Application;
import android.content.Context;

import com.techery.spares.application.BaseApplicationWithInjector;
import com.techery.spares.module.qualifier.ForApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;

@Module(
        includes = {
                // base helpers and drivers
                EventBusModule.class,
                EventDelegateModule.class,
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
    public Application provideApplication(Application application) {
        return application;
    }

    @Provides
    @ForApplication
    Context provideAppContext(Application application) {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    @ForApplication
    ObjectGraph provideObjectGraph(BaseApplicationWithInjector baseApplicationWithInjector) {
        return baseApplicationWithInjector.getObjectGraph();
    }

    @Provides
    @Singleton
    @ForApplication
    Injector provideInjector(BaseApplicationWithInjector baseApplicationWithInjector) {
        return baseApplicationWithInjector;
    }

}
