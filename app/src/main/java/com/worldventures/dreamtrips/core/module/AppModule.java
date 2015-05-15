package com.worldventures.dreamtrips.core.module;

import android.content.Context;
import android.content.SharedPreferences;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.regions.Regions;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.application.BaseApplicationWithInjector;
import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.InjectingApplicationModule;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.ComplexObjectStorage;
import com.techery.spares.storage.preferences.SimpleKeyValueStorage;
import com.worldventures.dreamtrips.App;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.ApiModule;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.DreamSpiceService;
import com.worldventures.dreamtrips.core.api.VideoCachingService;
import com.worldventures.dreamtrips.core.api.VideoCachingSpiceManager;
import com.worldventures.dreamtrips.core.initializer.FabricInitializer;
import com.worldventures.dreamtrips.core.initializer.FrescoInitializer;
import com.worldventures.dreamtrips.core.initializer.InstabugInitializer;
import com.worldventures.dreamtrips.core.initializer.LeakCanaryInitializer;
import com.worldventures.dreamtrips.core.initializer.LoggingInitializer;
import com.worldventures.dreamtrips.core.preference.Prefs;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.AvailableLocale;
import com.worldventures.dreamtrips.modules.video.VideoCachingDelegate;

import java.util.ArrayList;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module(
        injects = {
                App.class,
                InstabugInitializer.class,
                LeakCanaryInitializer.class,
                FabricInitializer.class,
                FrescoInitializer.class,
                DreamSpiceService.class,
                VideoCachingService.class,
                DreamSpiceManager.class,
                VideoCachingSpiceManager.class,
                VideoCachingDelegate.class,
        },
        includes = {
                InjectingApplicationModule.class,
                ApiModule.class
        },
        library = true,
        complete = false
)
public class AppModule {
    protected App app;

    public static final String LOCALES_KEY = "locales";

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    public App provideApplication() {
        return app;
    }

    @Provides
    public BaseApplicationWithInjector provideInjectingApplication() {
        return app;
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

    @Provides
    @Singleton
    public RefWatcher provideRefWatcher(App app) {
        return LeakCanary.install(app);
    }

    @Provides
    @Singleton
    public SessionHolder<UserSession> provideAppSessionHolder(SimpleKeyValueStorage simpleKeyValueStorage, @Global EventBus eventBus) {
        return new SessionHolder<>(simpleKeyValueStorage, UserSession.class, eventBus);
    }

    @Provides
    @Singleton
    public ComplexObjectStorage<ArrayList<AvailableLocale>> provideLocalesStorage(SimpleKeyValueStorage simpleKeyValueStorage) {
        return new ComplexObjectStorage<ArrayList<AvailableLocale>>(simpleKeyValueStorage,
                LOCALES_KEY,
                (Class<ArrayList<AvailableLocale>>) new ArrayList<AvailableLocale>().getClass());
    }

    @Provides
    @Singleton
    public SnappyRepository provideDB(Context context) {
        return new SnappyRepository(context);
    }

    @Provides
    @Singleton
    public Prefs providePrefs(SharedPreferences sharedPreferences) {
        return new Prefs(sharedPreferences);
    }

    @Provides
    public CognitoCachingCredentialsProvider provideCredProvider(Context context) {
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
    public TransferManager provideTransferManager(CognitoCachingCredentialsProvider credentialsProvider) {
        return new TransferManager(credentialsProvider);
    }
}
