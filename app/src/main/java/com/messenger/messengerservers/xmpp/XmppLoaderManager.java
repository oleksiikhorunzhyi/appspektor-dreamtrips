package com.messenger.messengerservers.xmpp;

import com.messenger.messengerservers.LoaderManager;
import com.messenger.messengerservers.loaders.Loader;
import com.messenger.messengerservers.loaders.ParticipantsLoader;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.model.MessengerUser;
import com.messenger.messengerservers.xmpp.loaders.XmppContactLoader;
import com.messenger.messengerservers.xmpp.loaders.XmppConversationListLoader;
import com.messenger.messengerservers.xmpp.loaders.XmppParticipantsLoader;
import com.messenger.messengerservers.xmpp.loaders.XmppConversationLoader;


public class XmppLoaderManager implements LoaderManager {
    private final XmppServerFacade facade;


    public XmppLoaderManager(XmppServerFacade facade) {
        this.facade = facade;
    }

    @Override
    public Loader<MessengerUser> createContactLoader() {
        return new XmppContactLoader(facade, null);
    }

    @Override
    public Loader<Conversation> createConversationLoader(String conversationId) {
        return new XmppConversationLoader(facade, conversationId);
    }

    @Override
    public Loader<Conversation> createConversationsLoader() {
        return new XmppConversationListLoader(facade);
    }

    @Override
    public ParticipantsLoader createParticipantsLoader() {
        return new XmppParticipantsLoader(facade);
    }

}
