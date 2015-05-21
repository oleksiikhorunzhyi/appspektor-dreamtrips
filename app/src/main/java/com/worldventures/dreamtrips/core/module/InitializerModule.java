package com.worldventures.dreamtrips.core.module;

import com.techery.spares.application.AppInitializer;
import com.worldventures.dreamtrips.core.initializer.FabricInitializer;
import com.worldventures.dreamtrips.core.initializer.FrescoInitializer;
import com.worldventures.dreamtrips.core.initializer.HockeyInitializer;
import com.worldventures.dreamtrips.core.initializer.InstabugInitializer;
import com.worldventures.dreamtrips.core.initializer.LeakCanaryInitializer;
import com.worldventures.dreamtrips.core.initializer.LoggingInitializer;
import com.worldventures.dreamtrips.core.initializer.SoftInputInitializer;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                HockeyInitializer.class,
                InstabugInitializer.class,
                LeakCanaryInitializer.class,
                FabricInitializer.class,
                FrescoInitializer.class,
                SoftInputInitializer.class,
        },
        library = true, complete = false)
public class InitializerModule {

    @Provides(type = Provides.Type.SET)
    AppInitializer provideEmptyInitializer() {
        return injector -> {
            //nothing to do here
        };
    }

    @Provides(type = Provides.Type.SET)
    AppInitializer provideSoftInputInitializer() {
        return new SoftInputInitializer();
    }

    @Provides(type = Provides.Type.SET)
    AppInitializer provideHockeyInitializer() {
        return new HockeyInitializer();
    }

    @Provides(type = Provides.Type.SET)
    public AppInitializer provideInstabugInitializer() {
        return new InstabugInitializer();
    }

    @Provides(type = Provides.Type.SET)
    public AppInitializer provideLeakCanaryInitializer() {
        return new LeakCanaryInitializer();
    }

    @Provides(type = Provides.Type.SET)
    public AppInitializer provideLoggingInitializer() {
        return new LoggingInitializer();
    }

    @Provides(type = Provides.Type.SET)
    public AppInitializer provideFabricInitializer() {
        return new FabricInitializer();
    }

    @Provides(type = Provides.Type.SET)
    public AppInitializer provideFrescoInitializer() {
        return new FrescoInitializer();
    }
}