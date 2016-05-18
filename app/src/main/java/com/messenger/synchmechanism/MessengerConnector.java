package com.messenger.synchmechanism;

import android.content.Context;

import com.github.pwittchen.networkevents.library.ConnectivityStatus;
import com.github.pwittchen.networkevents.library.NetworkEvents;
import com.github.pwittchen.networkevents.library.event.ConnectivityChanged;
import com.messenger.delegate.LoaderDelegate;
import com.messenger.messengerservers.ConnectionStatus;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.util.EventBusWrapper;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.util.ActivityWatcher;

import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.subjects.BehaviorSubject;

import static com.github.pwittchen.networkevents.library.ConnectivityStatus.MOBILE_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED_HAS_INTERNET;

public class MessengerConnector {

    private static MessengerConnector INSTANCE;
    //
    private final SessionHolder<UserSession> appSessionHolder;
    private final NetworkEvents networkEvents;
    //
    private final MessengerServerFacade messengerServerFacade;
    private final MessengerCacheSynchronizer messengerCacheSynchronizer;
    //
    private final BehaviorSubject<SyncStatus> connectionStream = BehaviorSubject.create(SyncStatus.DISCONNECTED);
    //
    private AtomicBoolean loadedGlobalConfigurations;

    private MessengerConnector(Context applicationContext, ActivityWatcher activityWatcher,
                               SessionHolder<UserSession> appSessionHolder, MessengerServerFacade messengerServerFacade,
                               LoaderDelegate loaderDelegate, EventBusWrapper eventBusWrapper) {

        this.appSessionHolder = appSessionHolder;
        this.messengerServerFacade = messengerServerFacade;
        this.messengerCacheSynchronizer = new MessengerCacheSynchronizer(loaderDelegate);
        this.networkEvents = new NetworkEvents(applicationContext, eventBusWrapper);
        this.loadedGlobalConfigurations = new AtomicBoolean(false);

        messengerServerFacade
                .getStatusObservable()
                .subscribe(this::handleFacadeStatus);

        activityWatcher.addOnStartStopListener(startStopAppListener);

        eventBusWrapper.register(this);
        networkEvents.register();
    }

    private void handleFacadeStatus(ConnectionStatus status) {
        switch (status) {
            case DISCONNECTED:
                connectionStream.onNext(SyncStatus.DISCONNECTED);
                break;
            case CONNECTING:
                connectionStream.onNext(SyncStatus.CONNECTING);
                break;
            case ERROR:
                connectionStream.onNext(SyncStatus.ERROR);
                break;
            case CONNECTED:
                syncData();
                break;
            default:
        }
    }

    public static MessengerConnector getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("You should initialize it");
        }
        return INSTANCE;
    }

    public static void init(Context applicationContext, ActivityWatcher activityWatcher,
                            SessionHolder<UserSession> appSessionHolder, MessengerServerFacade messengerServerFacade,
                            LoaderDelegate loaderDelegate, EventBusWrapper eventBusWrapper) {

        INSTANCE = new MessengerConnector(applicationContext, activityWatcher, appSessionHolder, messengerServerFacade,
                loaderDelegate, eventBusWrapper);
    }

    public Observable<SyncStatus> status() {
        return connectionStream.asObservable();
    }

    /**
     * This method should be called after loading all global configurations.
     * Also the one should be called if there is no need to load configurations
     */
    public void connectAfterGlobalConfig() {
        loadedGlobalConfigurations.set(true);
        connect();
    }

    /**
     * Should be called when we need to reconnect connection.
     */
    public void connect() {
        if (!loadedGlobalConfigurations.get()) return;
        if (messengerServerFacade.isConnected() || !isUserSessionPresent()) return;
        UserSession userSession = appSessionHolder.get().get();
        if (userSession.getUser() == null) return;
        messengerServerFacade.connect(userSession.getUsername(), userSession.getLegacyApiToken());
    }

    public void disconnect() {
        messengerServerFacade.disconnect();
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

    private boolean isUserSessionPresent() {
        return appSessionHolder != null && appSessionHolder.get() != null
                && appSessionHolder.get().isPresent();
    }

    private void syncData() {
        // TODO: 4/28/16 is this make sense?
        if (messengerServerFacade.sendInitialPresence()) {
            connectionStream.onNext(SyncStatus.SYNC_DATA);
            if (messengerServerFacade.sendInitialPresence()) {
                messengerCacheSynchronizer.updateCache(success -> {
                    messengerServerFacade.setActive(success);
                    connectionStream.onNext(SyncStatus.CONNECTED);
                });
            } else {
                connectionStream.onNext(SyncStatus.ERROR);
            }
        }
    }

    private final ActivityWatcher.OnStartStopAppListener startStopAppListener = new ActivityWatcher.OnStartStopAppListener() {
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
