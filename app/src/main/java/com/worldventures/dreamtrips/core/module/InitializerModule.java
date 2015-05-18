package com.worldventures.dreamtrips.core.module;

import com.techery.spares.application.AppInitializer;
import com.worldventures.dreamtrips.core.initializer.FabricInitializer;
import com.worldventures.dreamtrips.core.initializer.FrescoInitializer;
import com.worldventures.dreamtrips.core.initializer.InstabugInitializer;
import com.worldventures.dreamtrips.core.initializer.LeakCanaryInitializer;
import com.worldventures.dreamtrips.core.initializer.LifecycleInitializer;
import com.worldventures.dreamtrips.core.initializer.LoggingInitializer;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class InitializerModule {

    @Provides(type = Provides.Type.SET)
    AppInitializer provideEmptyInitializer() {
        return injector -> {
            //nothing to do here
        };
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
