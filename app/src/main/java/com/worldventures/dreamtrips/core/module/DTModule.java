package com.worldventures.dreamtrips.core.module;

import android.content.Context;
import android.content.SharedPreferences;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.application.BaseApplicationWithInjector;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.InjectingApplicationModule;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.DreamTripsApplication;
import com.worldventures.dreamtrips.core.api.spice.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.spice.DreamSpiceService;
import com.worldventures.dreamtrips.core.initializer.ImageLoaderInitializer;
import com.worldventures.dreamtrips.core.initializer.InstabugInitializer;
import com.worldventures.dreamtrips.core.initializer.LoggingInitializer;
import com.worldventures.dreamtrips.core.initializer.UploadingServiceInitializer;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.AppSessionHolder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import io.realm.Realm;

@Module(
        injects = {
                DreamTripsApplication.class,
                InstabugInitializer.class,
                ImageLoaderInitializer.class,
                UploadingServiceInitializer.class,
                DreamSpiceService.class,
                DreamSpiceManager.class
        },
        includes = {
                InjectingApplicationModule.class,
                ApiModule.class
        },
        library = true,
        complete = false
)
public class DTModule {
    DreamTripsApplication app;

    public DTModule(DreamTripsApplication app) {
        this.app = app;
    }

    @Provides
    DreamTripsApplication provideApplication() {
        return app;
    }

    @Provides
    BaseApplicationWithInjector provideInjectingApplication() {
        return app;
    }

    @Provides(type = Provides.Type.SET)
    AppInitializer provideImageLoaderInitializer() {
        return new ImageLoaderInitializer();
    }

    @Provides(type = Provides.Type.SET)
    AppInitializer provideInstabugInitializer() {
        return new InstabugInitializer();
    }

    @Provides(type = Provides.Type.SET)
    AppInitializer provideUploadingServiceInitializer() {
        return new UploadingServiceInitializer();
    }

    @Provides(type = Provides.Type.SET)
    AppInitializer provideLoggingInitializer() {
        return new LoggingInitializer();
    }

    @Provides
    Realm provideRealm(Context context) {
        return Realm.getInstance(context);
    }

    @Provides
    @Singleton
    AppSessionHolder provideAppSessionHolder(SimpleKeyValueStorage simpleKeyValueStorage, @Global EventBus eventBus) {
        return new AppSessionHolder(simpleKeyValueStorage, eventBus);
    }

    @Provides
    @Singleton
    SnappyRepository provideDB(Context context) {
        return new SnappyRepository(context);
    }

    @Provides
    @Singleton
    Prefs providePrefs(SharedPreferences sharedPreferences) {
        return new Prefs(sharedPreferences);
    }

}
