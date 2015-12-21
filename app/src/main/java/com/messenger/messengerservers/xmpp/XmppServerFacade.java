package com.messenger.messengerservers.xmpp;

import android.util.Log;

import com.messenger.messengerservers.AuthorizeStatus;
import com.messenger.messengerservers.ChatManager;
import com.messenger.messengerservers.ContactManager;
import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.LoaderManager;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.PaginationManager;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.parameters.ServerParameters;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.StringGanarator;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class XmppServerFacade implements MessengerServerFacade {

    private static final String TAG = "XmppServerFacade";

    private AbstractXMPPConnection connection;
    volatile AuthorizeStatus status;

    private final ExecutorService connectionExecutor = Executors.newSingleThreadExecutor();
    private final List<AuthorizeListener> onConnectListeners = new CopyOnWriteArrayList<>();

    private final LoaderManager loaderManager;
    private final PaginationManager paginationManager;
    private final GlobalEventEmitter globalEventEmitter;
    private final ChatManager chatManager;
    private final RosterManager rosterManager;

    public XmppServerFacade() {
        PingManager.setDefaultPingInterval(45);
        loaderManager = new XmppLoaderManager(this);
        paginationManager = new XmppPaginationManager(this);
        globalEventEmitter = new XmppGlobalEventEmitter(this);
        chatManager = new XmppChatManager(this);
        rosterManager = new RosterManager(this);
    }

    @Override
    public void authorizeAsync(String username, String password) {
        status = AuthorizeStatus.PROGRESS;
        Log.i("Xmpp Authorize with ", String.format("userName:%s password:%s", username, password));

        connectionExecutor.execute(() -> {
                    SASLAuthentication.registerSASLMechanism(new SASLWVMechanism());
                    connection = new XMPPTCPConnection(XMPPTCPConnectionConfiguration.builder()
                            .setUsernameAndPassword(username, password)
                            .setServiceName(JidCreatorHelper.SERVICE_NAME)
                            .setHost(ServerParameters.URL)
                            .setResource(StringGanarator.getRandomString(5))
                            .setDebuggerEnabled(true)
                            .setPort(ServerParameters.PORT)
                            .setSendPresence(false)
                            .build());
                    connection.setPacketReplyTimeout(15000);

                    try {
                        connection.connect();
                        connection.login();
                        setPresenceStatus(true);

                        status = AuthorizeStatus.SUCCESS;
                        for (AuthorizeListener listener : onConnectListeners) {
                            listener.onSuccess();
                        }
                    } catch (SmackException | IOException | XMPPException e) {
                        status = AuthorizeStatus.FAILED;
                        for (AuthorizeListener listener : onConnectListeners) {
                            listener.onFailed(e);
                        }
                        Log.e(TAG, "XMPP server", e);
                    }
                }
        );
    }

    @Override
    public void disconnectAsync() {
        connectionExecutor.execute(connection::disconnect);
    }

    @Override
    public boolean isAuthorized() {
        return status == AuthorizeStatus.SUCCESS;
    }

    @Override
    public void addAuthorizationListener(AuthorizeListener listener) {
        onConnectListeners.add(listener);
    }

    @Override
    public void removeAuthorizationListener(AuthorizeListener listener) {
        onConnectListeners.remove(listener);
    }

    @Override
    public void setPresenceStatus(boolean active) {
        try {
            connection.sendStanza(new Presence(active ? Presence.Type.available : Presence.Type.unavailable));
        } catch (SmackException.NotConnectedException e) {
            Log.w(TAG, "setPresenceStatus", e);
        }
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
    public ContactManager getContactManager() {
        return rosterManager;
    }

    @Override
    public GlobalEventEmitter getGlobalEventEmitter() {
        return globalEventEmitter;
    }

    @Override
    public User getOwner() {
        return new User(connection.getUser().split("@")[0]);
    }

    public AbstractXMPPConnection getConnection() {
        return connection;
    }
}
