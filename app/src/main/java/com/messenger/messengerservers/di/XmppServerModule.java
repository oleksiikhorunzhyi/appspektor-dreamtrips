package com.messenger.messengerservers.di;


import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.ui.presenter.NewChatLayoutPresenterImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true,
        complete = false,
        injects = {NewChatLayoutPresenterImpl.class})

public class XmppServerModule {

    @Singleton
    @Provides
    MessengerServerFacade provideXmppServerFacade(){
        return new XmppServerFacade();
    }
}
