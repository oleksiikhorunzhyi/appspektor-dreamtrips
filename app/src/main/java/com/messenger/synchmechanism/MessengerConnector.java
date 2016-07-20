package com.messenger.synchmechanism;

import android.content.Context;

import com.github.pwittchen.networkevents.library.ConnectivityStatus;
import com.github.pwittchen.networkevents.library.NetworkEvents;
import com.github.pwittchen.networkevents.library.event.ConnectivityChanged;
import com.messenger.messengerservers.ConnectionStatus;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.util.EventBusWrapper;
import com.messenger.util.SessionHolderHelper;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.util.ActivityWatcher;

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
    private final MessengerServerFacade messengerServerFacade;
    private final SessionHolder<UserSession> appSessionHolder;
    private final MessengerSyncDelegate messengerSyncDelegate;

    @Inject
    MessengerConnector(Context applicationContext, ActivityWatcher activityWatcher,
                       SessionHolder<UserSession> appSessionHolder, MessengerServerFacade messengerServerFacade,
                       EventBusWrapper eventBusWrapper, MessengerSyncDelegate messengerSyncDelegate) {
        this.appSessionHolder = appSessionHolder;
        this.messengerServerFacade = messengerServerFacade;
        this.messengerSyncDelegate = messengerSyncDelegate;
        NetworkEvents networkEvents = new NetworkEvents(applicationContext, eventBusWrapper);

        messengerServerFacade
                .getStatusObservable()
                .subscribe(this::handleConnectionStatus);


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

    private void handleConnectionStatus(ConnectionStatus status) {
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

    public Observable<ConnectionStatus> getAuthToServerStatus() {
        return messengerServerFacade.getStatusObservable();
    }

    public void connect() {
        if (messengerServerFacade.isConnected() || !SessionHolderHelper.hasEntity(appSessionHolder))
            return;
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

    private void syncData() {
        if (messengerServerFacade.sendInitialPresence()) {
            connectionStream.onNext(SyncStatus.SYNC_DATA);
            messengerSyncDelegate.sync()
                    .subscribe(result -> {
                        Timber.d("Sync succeed");
                        messengerServerFacade.setActive(result);
                        connectionStream.onNext(SyncStatus.CONNECTED);
                    }, e -> {
                        disconnect();
                        Timber.e(e, "Sync failed");
                        connectionStream.onNext(SyncStatus.ERROR);
                    });
        } else {
            Timber.e("Sync failed");
            connectionStream.onNext(SyncStatus.ERROR);
        }
    }
}
