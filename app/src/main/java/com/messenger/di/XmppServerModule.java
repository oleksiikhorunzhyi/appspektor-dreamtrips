package com.messenger.di;

import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.presenter.AddChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.BaseNewChatMembersScreenPresenter;
import com.messenger.ui.presenter.ChatGroupScreenPresenter;
import com.messenger.ui.presenter.ChatScreenPresenterImpl;
import com.messenger.ui.presenter.ChatSingleScreenPresenter;
import com.messenger.ui.presenter.ConversationListScreenPresenterImpl;
import com.messenger.ui.presenter.NewChatScreenPresenterImpl;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;

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

                BaseNewChatMembersScreenPresenter.class,
                NewChatScreenPresenterImpl.class,
                AddChatMembersScreenPresenterImpl.class,

                ConversationListScreenPresenterImpl.class,
                ChatActivity.class})

public class XmppServerModule {

    @Singleton
    @Provides
    MessengerServerFacade provideXmppServerFacade() {
        return new XmppServerFacade();
    }

    @Provides
    User provideUser(SessionHolder<UserSession> appSessionHolder) {
        return new User(appSessionHolder.get().get().getUser().getUsername());
    }
}
