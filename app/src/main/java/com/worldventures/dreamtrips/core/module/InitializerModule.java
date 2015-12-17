package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.messenger.di.ChatFacadeInitializer;
import com.messenger.di.PresenceListenerInitializer;
import com.messenger.di.StorageInitializer;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.core.initializer.BadgeCountObserverInitializer;
import com.worldventures.dreamtrips.core.initializer.FabricInitializer;
import com.worldventures.dreamtrips.core.initializer.FrescoInitializer;
import com.worldventures.dreamtrips.core.initializer.HockeyInitializer;
import com.worldventures.dreamtrips.core.initializer.InstabugInitializer;
import com.worldventures.dreamtrips.core.initializer.JodaTimeInitializer;
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
                BadgeCountObserverInitializer.class,
                JodaTimeInitializer.class,
                StorageInitializer.class,
                PresenceListenerInitializer.class

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
    AppInitializer provideJodaInitializer(Context context) {
        return new JodaTimeInitializer(context);
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

    @Provides(type = Provides.Type.SET)
    public AppInitializer provideBadgeCountObserverInitializer() {
        return new BadgeCountObserverInitializer();
    }

    @Provides(type = Provides.Type.SET)
    public AppInitializer provideStorageInitializer(@ForApplication Context context){
        return new StorageInitializer(context);
    }

    @Provides(type = Provides.Type.SET)
    public AppInitializer provideFacadeInitializer(){
        return new ChatFacadeInitializer();
    }

    @Provides(type = Provides.Type.SET)
    public AppInitializer providePresenceListenerInitializer() {
        return new PresenceListenerInitializer();
    }
}
