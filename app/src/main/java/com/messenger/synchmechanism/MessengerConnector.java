package com.messenger.synchmechanism;

import android.content.Context;

import com.github.pwittchen.networkevents.library.ConnectivityStatus;
import com.github.pwittchen.networkevents.library.NetworkEvents;
import com.github.pwittchen.networkevents.library.event.ConnectivityChanged;
import com.messenger.delegate.UserProcessor;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.listeners.ConnectionListener;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.util.EventBusWrapper;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.util.ActivityWatcher;

import rx.Observable;
import rx.subjects.ReplaySubject;

import static com.github.pwittchen.networkevents.library.ConnectivityStatus.MOBILE_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED;
import static com.github.pwittchen.networkevents.library.ConnectivityStatus.WIFI_CONNECTED_HAS_INTERNET;
import static com.messenger.synchmechanism.ConnectionStatus.CONNECTED;
import static com.messenger.synchmechanism.ConnectionStatus.CONNECTING;
import static com.messenger.synchmechanism.ConnectionStatus.DISCONNECTED;
import static com.messenger.synchmechanism.ConnectionStatus.ERROR;

public class MessengerConnector {

    private static MessengerConnector INSTANCE;
    //
    private Context applicationContext;
    private SessionHolder<UserSession> appSessionHolder;
    private DreamSpiceManager spiceManager;
    private NetworkEvents networkEvents;
    //
    private MessengerServerFacade messengerServerFacade;
    private MessengerCacheSynchronizer messengerCacheSynchronizer;
    //
    private ReplaySubject<ConnectionStatus> connectionStream;

    private MessengerConnector(Context applicationContext, ActivityWatcher activityWatcher,
                               SessionHolder<UserSession> appSessionHolder, MessengerServerFacade messengerServerFacade,
                               DreamSpiceManager spiceManager,
                               ConversationsDAO conversationsDAO, ParticipantsDAO participantsDAO,
                               MessageDAO messageDAO, UsersDAO usersDAO,
                               EventBusWrapper eventBusWrapper) {

        this.applicationContext = applicationContext;
        this.appSessionHolder = appSessionHolder;
        this.messengerServerFacade = messengerServerFacade;
        this.spiceManager = spiceManager;
        this.messengerCacheSynchronizer = new MessengerCacheSynchronizer(messengerServerFacade, new UserProcessor(spiceManager), conversationsDAO, participantsDAO, messageDAO, usersDAO);
        this.networkEvents = new NetworkEvents(applicationContext, eventBusWrapper);
        this.connectionStream = ReplaySubject.create(1);

        messengerServerFacade.addAuthorizationListener(authListener);
        messengerServerFacade.addConnectionListener(connectionListener);

        activityWatcher.addOnStartStopListener(startStopAppListener);

        eventBusWrapper.register(this);
        networkEvents.register();
    }

    public static MessengerConnector getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("You should initialize it");
        }
        return INSTANCE;
    }

    public static void init(Context applicationContext, ActivityWatcher activityWatcher,
                            SessionHolder<UserSession> appSessionHolder, MessengerServerFacade messengerServerFacade,
                            DreamSpiceManager spiceManager, ConversationsDAO conversationsDAO, ParticipantsDAO participantsDAO,
                            MessageDAO messageDAO, UsersDAO usersDAO, EventBusWrapper eventBusWrapper) {

        INSTANCE = new MessengerConnector(applicationContext, activityWatcher, appSessionHolder, messengerServerFacade,
                spiceManager, conversationsDAO, participantsDAO, messageDAO, usersDAO, eventBusWrapper);
    }

    public Observable<ConnectionStatus> status() {
        return connectionStream.asObservable();
    }

    public void connect() {
        if (appSessionHolder == null
                || appSessionHolder.get() == null || !appSessionHolder.get().isPresent()
                || connectionStream.getValue() == CONNECTING || connectionStream.getValue() == CONNECTED) {
            return;
        }

        connectionStream.onNext(CONNECTING);
        UserSession userSession = appSessionHolder.get().get();
        if (userSession.getUser() == null) return;
        messengerServerFacade.authorizeAsync(userSession.getUsername(), userSession.getLegacyApiToken());
    }

    public void disconnect() {
        if (connectionStream.getValue() != CONNECTED && connectionStream.getValue() != CONNECTING) return;
        //
        if (spiceManager.isStarted()) spiceManager.shouldStop();
        messengerServerFacade.disconnectAsync();

        if (connectionStream.getValue() != DISCONNECTED) {
            connectionStream.onNext(DISCONNECTED);
        }
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
            messengerCacheSynchronizer.updateCache(success -> {
                messengerServerFacade.setPresenceStatus(success);
                connectionStream.onNext(success ? CONNECTED : ERROR);
            });
        }

        @Override
        public void onFailed(Exception exception) {
            super.onFailed(exception);
            connectionStream.onNext(ERROR);
        }
    };

    final private ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void onDisconnected() {
            connectionStream.onNext(DISCONNECTED);
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
