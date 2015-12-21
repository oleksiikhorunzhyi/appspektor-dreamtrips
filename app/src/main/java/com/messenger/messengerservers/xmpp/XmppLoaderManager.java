package com.messenger.messengerservers.xmpp;

import com.messenger.messengerservers.LoaderManager;
import com.messenger.messengerservers.entities.ConversationWithParticipants;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.listeners.AuthorizeListener;
import com.messenger.messengerservers.loaders.Loader;
import com.messenger.messengerservers.xmpp.loaders.XmppContactLoader;
import com.messenger.messengerservers.xmpp.loaders.XmppConversationLoader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class XmppLoaderManager implements LoaderManager {
    private final List<Loader> loaders = new CopyOnWriteArrayList<>();
    private final XmppServerFacade facade;

    private final AuthorizeListener authorizeListener = new AuthorizeListener() {
        @Override
        public void onSuccess() {
            super.onSuccess();
            for (Loader loader : loaders) {
                loader.load();
            }
        }
    };

    public XmppLoaderManager(XmppServerFacade facade) {
        this.facade = facade;
        facade.addAuthorizationListener(authorizeListener);
    }

    @Override
    public Loader<User> createContactLoader() {
        Loader<User> loader  = new XmppContactLoader(facade, null);
        loaders.add(loader);
        return loader;
    }

    @Override
    public Loader<ConversationWithParticipants> createConversationLoader() {
        Loader<ConversationWithParticipants> loader= new XmppConversationLoader(facade);
        loaders.add(loader);
        return loader;
    }

    @Override
    public void destroyLoader(Loader loader) {
        loaders.remove(loader);
    }

    @Override
    public void close() {
        facade.removeAuthorizationListener(authorizeListener);
    }
}
