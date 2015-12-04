package com.messenger.messengerservers.xmpp;

import org.jivesoftware.smack.AbstractXMPPConnection;

import com.messenger.messengerservers.LoaderManager;
import com.messenger.messengerservers.entities.Conversation;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.loaders.Loader;
import com.messenger.messengerservers.xmpp.loaders.XmppContactLoader;
import com.messenger.messengerservers.xmpp.loaders.XmppConversationLoader;

public class XmppLoaderManager implements LoaderManager {

    AbstractXMPPConnection connection;

    public XmppLoaderManager(AbstractXMPPConnection connection) {
        this.connection = connection;
    }

    @Override
    public Loader<User> getContactLoader() {
        return new XmppContactLoader(connection, null);
    }

    @Override
    public Loader<Conversation> getConversationLoader() {
        return new XmppConversationLoader(connection);
    }
}
