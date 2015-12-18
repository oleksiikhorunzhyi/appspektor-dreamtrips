package com.messenger.di;


import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.presenter.ChatGroupScreenPresenter;
import com.messenger.ui.presenter.ChatScreenPresenterImpl;
import com.messenger.ui.presenter.ChatSingleScreenPresenter;
import com.messenger.ui.presenter.ConversationListScreenPresenterImpl;
import com.messenger.ui.presenter.NewChatLayoutPresenterImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true,
        complete = false,
        injects = {
                ChatScreenPresenterImpl.class,
                ChatSingleScreenPresenter.class,
                ChatGroupScreenPresenter.class,

                ChatFacadeInitializer.class,

                NewChatLayoutPresenterImpl.class,
                ConversationListScreenPresenterImpl.class,
                ChatActivity.class})

public class XmppServerModule {

    @Singleton
    @Provides
    MessengerServerFacade provideXmppServerFacade() {
        return new XmppServerFacade();
    }

    @Singleton
    @Provides
    User provideUser() {
        return new User("techery_user6");
    }
}
