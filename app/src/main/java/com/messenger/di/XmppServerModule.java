package com.messenger.di;


import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.presenter.ConversationListScreenPresenterImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true,
        complete = false,
        injects = {ConversationListScreenPresenterImpl.class,
                ChatActivity.class})

public class XmppServerModule {

    @Singleton
    @Provides
    MessengerServerFacade provideXmppServerFacade() {
        return new XmppServerFacade();
    }
}
