package com.messenger.initializer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.messenger.delegate.LoaderDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.notification.UnhandledMessageWatcher;
import com.messenger.synchmechanism.MessengerConnector;
import com.messenger.util.EventBusWrapper;
import com.techery.spares.application.AppInitializer;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.utils.SimpleActivityLifecycleCallbacks;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.util.ActivityWatcher;

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
    ActivityWatcher watcher;
    @Inject
    LoaderDelegate loaderDelegate;
    @Inject
    EventBusWrapper eventBusWrapper;
    //
    @Inject
    Application app;
    @Inject
    UnhandledMessageWatcher unhandledMessageWatcher;

    @Override
    public void initialize(Injector injector) {
        injector.inject(this);
        //
        MessengerConnector.init(context, watcher, appSessionHolder, messengerServerFacade, loaderDelegate, eventBusWrapper);
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
