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

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

import static com.github.pwittchen.networkevents.library.ConnectivityStatus.MOBILE_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED_HAS_INTERNET;

@Singleton
public class MessengerConnector {
    private final BehaviorSubject<SyncStatus> connectionStream = BehaviorSubject.create(SyncStatus.DISCONNECTED);
    private final MessengerCacheSynchronizer messengerCacheSynchronizer;
    private final MessengerServerFacade messengerServerFacade;
    private final SessionHolder<UserSession> appSessionHolder;

    @Inject MessengerConnector(Context applicationContext, ActivityWatcher activityWatcher,
                       SessionHolder<UserSession> appSessionHolder, MessengerServerFacade messengerServerFacade,
                       LoaderDelegate loaderDelegate, EventBusWrapper eventBusWrapper) {
        this.appSessionHolder = appSessionHolder;
        this.messengerServerFacade = messengerServerFacade;
        this.messengerCacheSynchronizer = new MessengerCacheSynchronizer(loaderDelegate);
        NetworkEvents networkEvents = new NetworkEvents(applicationContext, eventBusWrapper);

        messengerServerFacade
                .getStatusObservable()
                .subscribe(this::handleFacadeStatus);


        registerActivityWatcher(activityWatcher);
        eventBusWrapper.register(this);
        networkEvents.register();
    }

    private void registerActivityWatcher(ActivityWatcher activityWatcher) {
        ActivityWatcher.OnStartStopAppListener startStopAppListener = new ActivityWatcher.OnStartStopAppListener() {
            @Override
            public void onStartApplication() {
                connect();
            }

            @Override
            public void onStopApplication() {
                disconnect();
            }
        };
        activityWatcher.addOnStartStopListener(startStopAppListener);
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

    public Observable<SyncStatus> status() {
        return connectionStream.asObservable();
    }

    public void connect() {
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
}
