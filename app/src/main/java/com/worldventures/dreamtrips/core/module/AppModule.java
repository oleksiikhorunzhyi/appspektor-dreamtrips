package com.worldventures.dreamtrips.core.module;

import android.app.Application;
import android.content.Context;

import com.messenger.di.FlaggingModule;
import com.messenger.di.MessengerModule;
import com.techery.spares.application.BaseApplicationWithInjector;
import com.techery.spares.module.DebugModule;
import com.techery.spares.module.InjectingApplicationModule;
import com.techery.spares.module.qualifier.ForApplication;
import com.worldventures.dreamtrips.App;
import com.worldventures.dreamtrips.modules.common.ResponseSnifferModule;
import com.worldventures.dreamtrips.modules.gcm.ActionReceiverModule;
import com.worldventures.dreamtrips.modules.gcm.GcmModule;
import com.worldventures.dreamtrips.core.janet.JanetModule;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                App.class,
        },
        includes = {
                // base injection and helpers/drivers
                InjectingApplicationModule.class,
                //
                DebugModule.class,
                InitializerModule.class,
                HolderModule.class,
                PersistenceModule.class,
                ManagerModule.class,
                //
                ApiModule.class,
                AmazonModule.class,
                RetryModule.class,
                ApiFactoryModule.class,
                //
                UiBindingModule.class,
                //
                RouteCreatorModule.class,
                //
                GcmModule.class,
                ActionReceiverModule.class,
                //
                ResponseSnifferModule.class,
                BadgeCountObserverModule.class,
                //
                NavigationModule.class,
                //
                LocaleModule.class,
                AppVersionNameModule.class,
                //
                MessengerModule.class,
                //
                JanetModule.class,
                //
                FlaggingModule.class
        },
        library = true,
        complete = false,
        overrides = true
)
public class AppModule {

    protected App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    Application provideApplication() {
        return app;
    }

    @Provides
    BaseApplicationWithInjector appWithInjector() {
        return app;
    }

    @ForApplication
    @Provides
    Context provideAppContext() {
        return app.getApplicationContext();
    }

    @Provides
    Context provideContext() {
        return app.getApplicationContext();
    }

}
