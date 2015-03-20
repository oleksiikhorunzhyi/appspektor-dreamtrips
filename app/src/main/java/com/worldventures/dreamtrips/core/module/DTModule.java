package com.worldventures.dreamtrips.core.module;

import android.content.Context;
import android.content.SharedPreferences;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.regions.Regions;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.application.BaseApplicationWithInjector;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.InjectingApplicationModule;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.DreamTripsApplication;
import com.worldventures.dreamtrips.core.api.spice.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.spice.DreamSpiceService;
import com.worldventures.dreamtrips.core.api.spice.DreamTripsRequest;
import com.worldventures.dreamtrips.core.initializer.FabricInitializer;
import com.worldventures.dreamtrips.core.initializer.ImageLoaderInitializer;
import com.worldventures.dreamtrips.core.initializer.InstabugInitializer;
import com.worldventures.dreamtrips.core.initializer.LoggingInitializer;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.uploader.UploadingFileManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module(
        injects = {
                DreamTripsApplication.class,
                InstabugInitializer.class,
                ImageLoaderInitializer.class,
                DreamSpiceService.class,
                DreamSpiceManager.class,
                DreamTripsRequest.UploadTripPhoto.class

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
    AppInitializer provideLoggingInitializer() {
        return new LoggingInitializer();
    }

    @Provides(type = Provides.Type.SET)
    AppInitializer provideFabricInitializer() {
        return new FabricInitializer();
    }

    @Provides
    @Singleton
    SessionHolder<UserSession> provideAppSessionHolder(SimpleKeyValueStorage simpleKeyValueStorage, @Global EventBus eventBus) {
        return new SessionHolder<>(simpleKeyValueStorage, UserSession.class, eventBus);
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


    @Provides
    CognitoCachingCredentialsProvider provideCredProvider(Context context) {
        return new CognitoCachingCredentialsProvider(
                context,
                BuildConfig.AWS_ACCOUNT_ID,
                BuildConfig.COGNITO_POOL_ID,
                BuildConfig.COGNITO_ROLE_UNAUTH,
                null,
                Regions.US_EAST_1);
    }

    @Provides
    @Singleton
    TransferManager provideTransferManager(CognitoCachingCredentialsProvider credentialsProvider) {
        return new TransferManager(credentialsProvider);
    }


    @Provides
    UploadingFileManager provideUploadingFileManager(Context context) {
        return new UploadingFileManager(context);
    }
}
