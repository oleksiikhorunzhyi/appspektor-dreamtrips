package com.messenger.synchmechanism;

import android.content.Context;

import com.github.pwittchen.networkevents.library.ConnectivityStatus;
import com.github.pwittchen.networkevents.library.NetworkEvents;
import com.github.pwittchen.networkevents.library.event.ConnectivityChanged;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.util.EventBusWrapper;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.session.UserSession;

import rx.Observable;
import rx.subjects.ReplaySubject;

import static com.github.pwittchen.networkevents.library.ConnectivityStatus.MOBILE_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED_HAS_INTERNET;

import static com.github.pwittchen.networkevents.library.ConnectivityStatus.MOBILE_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED_HAS_INTERNET;

public class MessengerConnector {

    private static MessengerConnector object;

    private Context applicationContext;

    private SessionHolder<UserSession> appSessionHolder;
    private MessengerServerFacade messengerServerFacade;
    private DreamSpiceManager spiceManager;
    private MessengerCacheSynchronizer messengerCacheSynchronizer;
    private NetworkEvents networkEvents;

    private ReplaySubject<ConnectionStatus> connectionObservable;
    private ConnectionStatus currentStatus;

    private MessengerConnector(Context applicationContext, ActivityWatcher activityWatcher,
                               SessionHolder<UserSession> appSessionHolder, MessengerServerFacade messengerServerFacade,
                               DreamSpiceManager spiceManager, EventBusWrapper eventBusWrapper) {

        this.applicationContext = applicationContext;
        this.appSessionHolder = appSessionHolder;
        this.messengerServerFacade = messengerServerFacade;
        this.spiceManager = spiceManager;
        this.messengerCacheSynchronizer = new MessengerCacheSynchronizer(messengerServerFacade, spiceManager);
        this.networkEvents = new NetworkEvents(applicationContext, eventBusWrapper);
        this.connectionObservable = ReplaySubject.create(1);

        activityWatcher.addOnStartStopListener(startStopAppListener);

        eventBusWrapper.register(this);
        networkEvents.register();
    }

    public static MessengerConnector getInstance() {
        if (object == null) {
            throw new IllegalStateException("You should initialize it");
        }
        return object;
    }

    public static void init(Context applicationContext, ActivityWatcher activityWatcher,
                            SessionHolder<UserSession> appSessionHolder, MessengerServerFacade messengerServerFacade,
                            DreamSpiceManager spiceManager, EventBusWrapper eventBusWrapper) {

        object = new MessengerConnector(applicationContext, activityWatcher, appSessionHolder, messengerServerFacade,
                spiceManager, eventBusWrapper);
    }

    public Observable<ConnectionStatus> subscribe() {
        return connectionObservable.asObservable();
    }

    public void connect() {
        if (messengerServerFacade.isAuthorized() || appSessionHolder == null
                || appSessionHolder.get() == null || !appSessionHolder.get().isPresent()
                || currentStatus == ConnectionStatus.CONNECTING || currentStatus == ConnectionStatus.CONNECTED) {
            return;
        }

        connectionObservable.onNext(currentStatus = ConnectionStatus.CONNECTING);
        messengerServerFacade.addAuthorizationListener(authListener);
        UserSession userSession = appSessionHolder.get().get();
        messengerServerFacade.authorizeAsync(userSession.getUsername(), userSession.getLegacyApiToken());
    }

    public void disconnect() {
        if (messengerServerFacade.isAuthorized()) {
            if (spiceManager.isStarted()) spiceManager.shouldStop();
            messengerServerFacade.disconnectAsync();
        }

        if (currentStatus != ConnectionStatus.DISCONNECTED) {
            connectionObservable.onNext(currentStatus = ConnectionStatus.DISCONNECTED);
        }
        messengerServerFacade.removeAuthorizationListener(authListener);
    }

    public void onEvent(ConnectivityChanged event) {
        ConnectivityStatus status = event.getConnectivityStatus();
        boolean internetConnected = status == MOBILE_CONNECTED || status == WIFI_CONNECTED_HAS_INTERNET || status == WIFI_CONNECTED;
        if (internetConnected) {
            connect();
        } else {
            disconnect();
        }
    }

    final private AuthorizeListener authListener = new AuthorizeListener() {
        @Override
        public void onSuccess() {
            if (!spiceManager.isStarted()) spiceManager.start(applicationContext);
            messengerCacheSynchronizer.updateCache(result -> {
                messengerServerFacade.setPresenceStatus(result);
                connectionObservable.onNext(currentStatus = ConnectionStatus.CONNECTED);
            });
        }

        @Override
        public void onFailed(Exception exception) {
            super.onFailed(exception);
            connectionObservable.onNext(currentStatus = ConnectionStatus.ERROR);
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
