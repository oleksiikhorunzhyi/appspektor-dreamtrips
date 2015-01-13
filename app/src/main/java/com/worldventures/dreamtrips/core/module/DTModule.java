package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.techery.spares.application.AppInitializer;
import com.techery.spares.application.BaseApplicationWithInjector;
import com.techery.spares.module.InjectingApplicationModule;
import com.worldventures.dreamtrips.DTApplication;
import com.worldventures.dreamtrips.core.initializer.ImageLoaderInitializer;
import com.worldventures.dreamtrips.core.initializer.LoggingInitializer;
import com.worldventures.dreamtrips.core.initializer.UploadingServiceInitializer;
import com.worldventures.dreamtrips.core.uploader.UploadingService;
import com.worldventures.dreamtrips.view.activity.LaunchActivity;
import com.worldventures.dreamtrips.view.activity.LoginActivity;
import com.worldventures.dreamtrips.view.activity.MainActivity;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

@Module(injects = {
        DTApplication.class,
        LaunchActivity.class,
        MainActivity.class,
        LoginActivity.class,

        ImageLoaderInitializer.class,
        UploadingServiceInitializer.class
},
        includes = {
                InjectingApplicationModule.class,
                ApiModule.class
        },
        library = true,
        complete = false
)
public class DTModule {
    DTApplication app;

    public DTModule(DTApplication app) {
        this.app = app;
    }

    @Provides
    DTApplication provideApplication() {
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
    AppInitializer provideUploadingServiceInitializer() {
        return new UploadingServiceInitializer();
    }

    @Provides(type = Provides.Type.SET)
    AppInitializer provideLoggingInitializer() {
        return new LoggingInitializer();
    }

    @Provides
    Realm provideRealm(Context context) {
        Realm realm = Realm.getInstance(context);
        return realm;
    }
}
