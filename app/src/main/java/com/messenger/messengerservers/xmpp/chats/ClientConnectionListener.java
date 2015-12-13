package com.messenger.messengerservers.xmpp.chats;


import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.xmpp.XmppServerFacade;

public class ClientConnectionListener extends AuthorizeListener {
    private final ConnectionClient connectionClient;
    private final XmppServerFacade facade;

    public ClientConnectionListener(XmppServerFacade facade, ConnectionClient connectionClient) {
        this.connectionClient = connectionClient;
        this.facade = facade;
    }

    @Override
    public void onSuccess() {
        super.onSuccess();
        connectionClient.setConnection(facade.getConnection());
        facade.removeAuthorizationListener(this);
    }
}
