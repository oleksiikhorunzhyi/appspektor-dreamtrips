package com.messenger.synchmechanism;

import android.content.Context;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.session.UserSession;

import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class MessengerConnector {

    private static MessengerConnector object;

    private Context applicationContext;

    private SessionHolder<UserSession> appSessionHolder;
    private MessengerServerFacade messengerServerFacade;
    private DreamSpiceManager spiceManager;
    private CacheSynchronizer cacheSynchronizer;

    private PublishSubject<Integer> connectionObservable;

    private MessengerConnector(Context applicationContext, ActivityWatcher activityWatcher,
                               SessionHolder<UserSession> appSessionHolder, MessengerServerFacade messengerServerFacade,
                               DreamSpiceManager spiceManager) {

        this.applicationContext = applicationContext;
        this.appSessionHolder = appSessionHolder;
        this.messengerServerFacade = messengerServerFacade;
        this.spiceManager = spiceManager;
        this.cacheSynchronizer = new CacheSynchronizer(messengerServerFacade, spiceManager);

        activityWatcher.addOnStartStopListener(startStopAppListener);

        connectionObservable = PublishSubject.create();
    }

    public static MessengerConnector getInstance() {
        if (object == null) {
            throw new IllegalStateException("You should initialize it");
        }
        return object;
    }

    public static void init(Context applicationContext, ActivityWatcher activityWatcher,
                            SessionHolder<UserSession> appSessionHolder, MessengerServerFacade messengerServerFacade,
                            DreamSpiceManager spiceManager) {

        object = new MessengerConnector(applicationContext, activityWatcher, appSessionHolder, messengerServerFacade,
                spiceManager);
    }

    public Subscription subscribe(Action1<? super Integer> onNext) {
        return connectionObservable.subscribe(onNext);
    }

    public void connect() {
        if (messengerServerFacade.isAuthorized() || appSessionHolder == null ||
                appSessionHolder.get() == null || !appSessionHolder.get().isPresent()) {
            return;
        }

        connectionObservable.onNext(Status.CONNECTING);
        messengerServerFacade.addAuthorizationListener(authListener);
        UserSession userSession = appSessionHolder.get().get();
        messengerServerFacade.authorizeAsync(userSession.getUsername(), userSession.getLegacyApiToken());
    }

    public void disconnect() {
        if (messengerServerFacade.isAuthorized()) {
            spiceManager.shouldStop();
            messengerServerFacade.disconnectAsync();
            connectionObservable.onNext(Status.DISCONNECTED);
        }

        messengerServerFacade.removeAuthorizationListener(authListener);
    }

    final private AuthorizeListener authListener = new AuthorizeListener() {
        @Override
        public void onSuccess() {
            if (!spiceManager.isStarted()) spiceManager.start(applicationContext);
            messengerCacheSynchronizer.updateCache(result -> {
                messengerServerFacade.setPresenceStatus(result);
                connectionObservable.onNext(Status.CONNECTED);
            });
        }

        @Override
        public void onFailed(Exception exception) {
            super.onFailed(exception);
            connectionObservable.onNext(Status.ERROR);
        }
    };

    final private ActivityWatcher.OnStartStopAppListener startStopAppListener = new ActivityWatcher.OnStartStopAppListener() {
        @Override
        public void onStartApplication() {
            connect();
        }

        @Override
        public void onStopApplication() {
            disconnect();
        }
    };

}
