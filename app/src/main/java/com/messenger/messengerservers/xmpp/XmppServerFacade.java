package com.messenger.messengerservers.xmpp;

import android.content.Context;
import android.net.SSLCertificateSocketFactory;
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
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.StringGenerator;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.util.CrashlyticsTracker;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;

import org.jivesoftware.smack.AbstractConnectionClosedListener;
import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
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

    private Context context;
    private XmppServerParams serverParams;
    private DreamSpiceManager requester;
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

    public XmppServerFacade(XmppServerParams serverParams, Context context, DreamSpiceManager requester, UsersDAO usersDAO) {
        gson = new GsonBuilder().registerTypeAdapter(AttachmentHolder.class, new GsonAttachmentAdapter()).create();
        this.context = context;
        this.serverParams = serverParams;
        this.requester = requester;
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
                        if (!requester.isStarted()) requester.start(context);
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
    public void disconnectAsync() {
        if (requester.isStarted()) requester.shouldStop();
        if (connection == null) return; // skip if not connected yet
        connectionExecutor.execute(connection::disconnect);
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
    public boolean setPresenceStatus(boolean active) {
        try {
            connection.sendStanza(new Presence(active ? Presence.Type.available : Presence.Type.unavailable));
            return this.active = true;
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
