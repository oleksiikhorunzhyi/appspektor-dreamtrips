package com.messenger.messengerservers.xmpp;

import android.net.SSLCertificateSocketFactory;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.innahema.collections.query.queriables.Queryable;
import com.messenger.messengerservers.ChatManager;
import com.messenger.messengerservers.LoaderManager;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.PaginationManager;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.listeners.ConnectionListener;
import com.messenger.messengerservers.model.AttachmentHolder;
import com.messenger.messengerservers.xmpp.providers.GsonAttachmentAdapter;
import com.messenger.messengerservers.xmpp.stanzas.outgoing.InitialPresence;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.StringGenerator;
import com.messenger.util.CrashlyticsTracker;
import com.worldventures.dreamtrips.BuildConfig;

import org.jivesoftware.smack.AbstractConnectionClosedListener;
import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class XmppServerFacade implements MessengerServerFacade {

    private static final long PACKET_REPLAY_TIMEOUT = TimeUnit.SECONDS.toMillis(60L);
    private static final int TIME_PING_INTERVAL = 45; // secs
    private static final String WV_API_PROTOCOL_VERSION = "1.0";

    private XmppServerParams serverParams;
    private AbstractXMPPConnection connection;
    private volatile boolean active;

    private final ExecutorService connectionExecutor = Executors.newSingleThreadExecutor();
    private final List<AuthorizeListener> authListeners = new CopyOnWriteArrayList<>();
    private final List<ConnectionListener> connectionListeners = new CopyOnWriteArrayList<>();

    private final LoaderManager loaderManager;
    private final PaginationManager paginationManager;
    private final XmppGlobalEventEmitter globalEventEmitter;
    private final ChatManager chatManager;
    private final Gson gson;

    public XmppServerFacade(XmppServerParams serverParams) {
        gson = new GsonBuilder().registerTypeAdapter(AttachmentHolder.class, new GsonAttachmentAdapter()).create();
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
        connection.addConnectionListener(connectionListener);
        connection.addAsyncStanzaListener(packet -> CrashlyticsTracker.trackError(new SmackException(packet.toString())),
                stanza -> stanza != null && stanza.getError() != null && !TextUtils.isEmpty(stanza.getError().toString()));
        return connection;
    }

    @Override
    public void authorizeAsync(String username, String password) {
        Timber.i("Logging in with %s->%s", username, password);
        connectionExecutor.execute(() -> {
                    try {
                        AbstractXMPPConnection connection = createConnection();
                        connection.connect();
                        connection.login(username, password);
                        //
                        Timber.i("Login success");
                        synchronized (XmppServerFacade.this) {
                            this.connection = connection;
                            for (AuthorizeListener listener : authListeners) {
                                listener.onSuccess();
                            }
                        }
                    } catch (SmackException | IOException | XMPPException e) {
                        Timber.w(e, "Login failed");
                        for (AuthorizeListener listener : authListeners) {
                            listener.onFailed(e);
                        }
                    }
                }
        );
    }

    @Override
    public void disconnectAsync(@Nullable Runnable callback) {
        if (connection == null) return; // skip if not connected yet
        connectionExecutor.execute(() -> {
            synchronized (XmppServerFacade.this) {
                connection.disconnect();
                if (callback != null) callback.run();
            }
        });
        active = false;
    }

    private AbstractConnectionListener connectionListener = new AbstractConnectionClosedListener() {

        @Override
        public void connected(XMPPConnection connection) {
            Queryable.from(connectionListeners).forEachR(ConnectionListener::onConnected);
        }

        @Override
        public void connectionTerminated() {
            Queryable.from(connectionListeners).forEachR(ConnectionListener::onDisconnected);
        }
    };

    @Override
    public synchronized boolean isAuthorized() {
        return connection != null && connection.isConnected() && connection.isAuthenticated();
    }

    @Override
    public void addAuthorizationListener(AuthorizeListener listener) {
        authListeners.add(listener);
    }

    @Override
    public void removeAuthorizationListener(AuthorizeListener listener) {
        authListeners.remove(listener);
    }

    @Override
    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    @Override
    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
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

    public AbstractXMPPConnection getConnection() {
        return connection;
    }

    public Gson getGson() {
        return gson;
    }
}
