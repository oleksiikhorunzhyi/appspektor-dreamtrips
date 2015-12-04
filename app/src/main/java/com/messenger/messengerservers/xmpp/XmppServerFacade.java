package com.messenger.messengerservers.xmpp;

import android.util.Log;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.OrFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.ping.PingManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.messenger.messengerservers.AuthorizeStatus;
import com.messenger.messengerservers.GlobalEventEmitter;
import com.messenger.messengerservers.LoaderManager;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.PaginationManager;
import com.messenger.messengerservers.chat.MultiUserChat;
import com.messenger.messengerservers.chat.SingleUserChat;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.listeners.GlobalMessageReceiver;
import com.messenger.messengerservers.parameters.ServerParameters;
import com.messenger.messengerservers.xmpp.chats.XmppMultiUserChat;
import com.messenger.messengerservers.xmpp.chats.XmppSingleUserChat;
import com.messenger.messengerservers.xmpp.util.JidCreatorHelper;
import com.messenger.messengerservers.xmpp.util.XmppMessageConverter;
import com.messenger.messengerservers.xmpp.util.XmppUtils;

public class XmppServerFacade implements MessengerServerFacade {
    private static final String TAG = "XmppServerFacade";
    private final ExecutorService connectionExecutor = Executors.newSingleThreadExecutor();
    private final List<AuthorizeListener> onConnectListeners = new CopyOnWriteArrayList<>();

    private GlobalMessageReceiver globalMessageReceiver;
    volatile AuthorizeStatus status;

    private AbstractXMPPConnection connection;
    private final StanzaFilter stanzaFilter = new OrFilter(MessageTypeFilter.CHAT, MessageTypeFilter.GROUPCHAT);

    private LoaderManager loaderManager;
    private PaginationManager paginatorManager;

    @Override
    public void authorizeAsync(String username, String password) {
        status = AuthorizeStatus.PROGRESS;

        connectionExecutor.execute(() -> {
                    PingManager.setDefaultPingInterval(45);
//                    SASLAuthentication.registerSASLMechanism(new SASLWVMechanism());
                    XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                            .setUsernameAndPassword(username, password)
                            .setServiceName(JidCreatorHelper.SERVICE_NAME)
                            .setHost(ServerParameters.URL)
                            .setResource("smack-client")
                            .setDebuggerEnabled(true)
                            .setPort(ServerParameters.PORT)
                            .setSendPresence(false)
                            .build();

                    connection = new XMPPTCPConnection(config);
                    loaderManager = new XmppLoaderManager(connection);
                    paginatorManager = new XmppPaginationManager(connection);
                    try {
                        connection.connect();
                        connection.login();

                        connection.addPacketInterceptor(packet -> {
                            packet.setStanzaId(XmppUtils.generateStanzaId());
                            packet.setFrom(JidCreatorHelper.obtainJid(new User(username)));
                            if (globalMessageReceiver != null) {
                                globalMessageReceiver.onSendMessage(XmppMessageConverter.convert((Message) packet));
                            }
                        }, stanzaFilter);

                        connection.addAsyncStanzaListener(packet -> {
                            if (globalMessageReceiver != null) {
                                globalMessageReceiver.onReceiveMessage(XmppMessageConverter.convert((Message) packet));
                            }
                        }, stanzaFilter);

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
    public SingleUserChat createSingleUserChat(User companion) {
        return new XmppSingleUserChat(connection, companion);
    }

    @Override
    public MultiUserChat createMultiUserChat(User owner) {
        return new XmppMultiUserChat(connection, owner);
    }

    @Override
    public GlobalEventEmitter getGlobalEventEmitter() {
        return null;
    }

    @Override
    public LoaderManager getLoaderManager() {
        return loaderManager;
    }

    @Override
    public PaginationManager getPaginatorManager() {
        return paginatorManager;
    }

    @Override
    public void setGlobalMessageReceiver(GlobalMessageReceiver globalMessageReceiver) {
        this.globalMessageReceiver = globalMessageReceiver;
    }

    @Override
    public void setPresenceStatus(boolean active) {
        try {
            connection.sendStanza(new Presence(active ? Presence.Type.available : Presence.Type.unavailable));
        } catch (SmackException.NotConnectedException e) {
            Log.w(TAG, "setPresenceStatus", e);
        }
    }

}
