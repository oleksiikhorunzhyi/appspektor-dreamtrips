package com.messenger.di;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.synchmechanism.ActivityWatcher;
import com.messenger.synchmechanism.MessengerConnector;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.utils.SimpleActivityLifecycleCallbacks;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Inject;

public class MessengerInitializer implements AppInitializer {

    @Inject
    @ForApplication
    Context context;
    @Inject
    SessionHolder<UserSession> appSessionHolder;
    @Inject
    MessengerServerFacade messengerServerFacade;
    @Inject
    DreamSpiceManager spiceManager;
    @Inject
    ActivityWatcher watcher;
    //
    @Inject
    Application app;
    @Inject
    UnhandledMessageWatcher unhandledMessageWatcher;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        //
        MessengerConnector.init(context, watcher, appSessionHolder, messengerServerFacade, spiceManager);
        //// TODO: 12/29/15 refactor
        app.registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {

            @Override
            public void onActivityStarted(Activity activity) {
                unhandledMessageWatcher.start(activity);
            }

            @Override
            public void onActivityStopped(Activity activity) {
                unhandledMessageWatcher.stop(activity);
            }
        });
    }
}
