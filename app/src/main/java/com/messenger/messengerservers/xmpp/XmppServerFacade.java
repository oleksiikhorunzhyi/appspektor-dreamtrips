package com.messenger.messengerservers.xmpp;

import android.net.SSLCertificateSocketFactory;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.messenger.messengerservers.ChatManager;
import com.messenger.messengerservers.ConnectionStatus;
import com.messenger.messengerservers.LoaderManager;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.PaginationManager;
import com.messenger.messengerservers.xmpp.stanzas.outgoing.InitialPresence;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.StringGenerator;
import com.messenger.util.CrashlyticsTracker;
import com.worldventures.dreamtrips.BuildConfig;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingManager;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class XmppServerFacade implements MessengerServerFacade {

    private static final long PACKET_REPLAY_TIMEOUT = TimeUnit.SECONDS.toMillis(60L);
    private static final int TIME_PING_INTERVAL = 45; // secs
    private static final String WV_API_PROTOCOL_VERSION = "1.0";

    private XmppServerParams serverParams;
    // TODO: 4/28/16 not rx way
    private AbstractXMPPConnection connection;
    private volatile boolean active;

    private final Executor executor = Executors.newSingleThreadExecutor();

    private final LoaderManager loaderManager;
    private final PaginationManager paginationManager;
    private final XmppGlobalEventEmitter globalEventEmitter;
    private final ChatManager chatManager;

    // TODO: 4/28/16 remove GSON, use converter interface
    private final Gson gson;

    private final BehaviorSubject<XMPPConnection> connectionSubject = BehaviorSubject.create();
    private final BehaviorSubject<ConnectionStatus> connectionStatusSubject = BehaviorSubject.create();

    public XmppServerFacade(XmppServerParams serverParams, Gson gson) {
        this.gson = gson;
        this.serverParams = serverParams;
        PingManager.setDefaultPingInterval(TIME_PING_INTERVAL);
        loaderManager = new XmppLoaderManager(this);
        paginationManager = new XmppPaginationManager(this);
        globalEventEmitter = new XmppGlobalEventEmitter(this);
        chatManager = new XmppChatManager(this);
    }

    private MessengerConnection createConnection() {
        SASLAuthentication.registerSASLMechanism(new SASLWVMechanism());
        MessengerConnection connection = new MessengerConnection(XMPPTCPConnectionConfiguration.builder()
                .setServiceName(JidCreatorHelper.SERVICE_NAME)
                .setHost(serverParams.host)
                .setPort(serverParams.port)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.required)
                .setSocketFactory(SSLCertificateSocketFactory.getDefault())
                .setResource(StringGenerator.getRandomString(5))
                .setSendPresence(false)
                .setDebuggerEnabled(BuildConfig.DEBUG)
                .build());
        connection.setPacketReplyTimeout(PACKET_REPLAY_TIMEOUT);
        connection.addAsyncStanzaListener(packet -> CrashlyticsTracker.trackError(new SmackException(packet.toString())),
                stanza -> stanza != null && stanza.getError() != null && !TextUtils.isEmpty(stanza.getError().toString()));
        return connection;
    }

    @Override
    public void connect(String username, String password) {
        Observable.create(new Observable.OnSubscribe<AbstractXMPPConnection>() {
            @Override
            public void call(Subscriber<? super AbstractXMPPConnection> subscriber) {
                connect(subscriber, username, password);
            }
        })
                .subscribeOn(Schedulers.from(executor))
                .subscribe(this::connected, this::connectionError);
    }

    private void connect(Subscriber<? super AbstractXMPPConnection> subscriber, String username, String password) {
        if (connection != null && connection.isConnected()) {
            subscriber.onCompleted();
            return;
        }
        try {
            connectionStatusSubject.onNext(ConnectionStatus.CONNECTING);
            AbstractXMPPConnection connection = createConnection();
            connection.connect();
            connection.login(username, password);
            subscriber.onNext(connection);
            subscriber.onCompleted();
            connectionStatusSubject.onNext(ConnectionStatus.CONNECTED);
        } catch (Exception e) {
            subscriber.onError(e);
            connectionStatusSubject.onNext(ConnectionStatus.ERROR);
        }
    }

    private void connected(AbstractXMPPConnection connection) {
        this.connection = connection;
        connectionSubject.onNext(connection);
        Timber.d("connected");
    }

    private void connectionError(Throwable throwable) {
        connectionSubject.onError(throwable);
        Timber.e(throwable, "connection error");
    }

    @Override
    public void disconnect() {
        Observable.create(this::disconnect)
                .subscribeOn(Schedulers.from(executor))
                .subscribe(o -> Timber.d("disconnected"), e -> Timber.e(e, "disconnect error"));
    }

    private void disconnect(Subscriber subscriber) {
        if (connection != null) {
            connection.disconnect();
            connectionStatusSubject.onNext(ConnectionStatus.DISCONNECTED);
            connection = null;
        }
        active = false;
        subscriber.onCompleted();
    }

    @Override
    public synchronized boolean isConnected() {
        return connection != null && connection.isConnected() && connection.isAuthenticated();
    }

    @Override
    public boolean sendInitialPresence() {
        try {
            connection.sendStanza(new InitialPresence(WV_API_PROTOCOL_VERSION));
            return true;
        } catch (SmackException.NotConnectedException e) {
            Timber.w(e, "Presence failed");
            return false;
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public ChatManager getChatManager() {
        return chatManager;
    }

    @Override
    public LoaderManager getLoaderManager() {
        return loaderManager;
    }

    @Override
    public PaginationManager getPaginationManager() {
        return paginationManager;
    }

    @Override
    public XmppGlobalEventEmitter getGlobalEventEmitter() {
        return globalEventEmitter;
    }

    @Override
    public String getUsername() {
        return connection.getUser().split("@")[0];
    }

    @Override
    public Observable<ConnectionStatus> getStatusObservable() {
        return connectionStatusSubject.asObservable();
    }

    public Gson getGson() {
        return gson;
    }

    public Observable<XMPPConnection> getConnectionObservable() {
        return connectionSubject;
    }
}
