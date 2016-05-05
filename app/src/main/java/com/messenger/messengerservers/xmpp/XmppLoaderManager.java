package com.messenger.messengerservers.xmpp;

import com.messenger.messengerservers.LoaderManager;
import com.messenger.messengerservers.loaders.ContactsLoader;
import com.messenger.messengerservers.loaders.ConversationLoader;
import com.messenger.messengerservers.xmpp.loaders.XmppContactLoader;
import com.messenger.messengerservers.xmpp.loaders.ConversationsListLoader;
import com.messenger.messengerservers.xmpp.loaders.ConversationsLoader;

public class XmppLoaderManager implements LoaderManager {
    private final XmppServerFacade facade;


    public XmppLoaderManager(XmppServerFacade facade) {
        this.facade = facade;
    }

    @Override
    public ContactsLoader createContactLoader() {
        return new XmppContactLoader(facade);
    }

    @Override
    public ConversationLoader createConversationLoader(String conversationId) {
        return new ConversationsLoader(facade, conversationId);
    }

    @Override
    public com.messenger.messengerservers.loaders.ConversationsLoader createConversationsLoader() {
        return new ConversationsListLoader(facade);
    }
}
