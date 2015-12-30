package com.messenger.messengerservers.xmpp;

import com.messenger.messengerservers.LoaderManager;
import com.messenger.messengerservers.entities.ConversationData;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.loaders.Loader;
import com.messenger.messengerservers.xmpp.loaders.XmppContactLoader;
import com.messenger.messengerservers.xmpp.loaders.XmppConversationLoader;


public class XmppLoaderManager implements LoaderManager {
    private final XmppServerFacade facade;


    public XmppLoaderManager(XmppServerFacade facade) {
        this.facade = facade;
    }

    @Override
    public Loader<User> createContactLoader() {
        return new XmppContactLoader(facade, null);
    }

    @Override
    public Loader<ConversationData> createConversationLoader() {
        return new XmppConversationLoader(facade);
    }

}
