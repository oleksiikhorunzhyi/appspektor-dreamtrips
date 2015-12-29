package com.messenger.di;

import android.content.Context;

import com.messenger.delegate.ChatDelegate;
import com.messenger.delegate.LeaveChatDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.User;
import com.messenger.messengerservers.xmpp.XmppServerFacade;
import com.messenger.synchmechanism.ActivityWatcher;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.presenter.AddChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.BaseNewChatMembersScreenPresenter;
import com.messenger.ui.presenter.ChatGroupScreenPresenter;
import com.messenger.ui.presenter.ChatScreenPresenterImpl;
import com.messenger.ui.presenter.ChatSettingsScreenPresenterImpl;
import com.messenger.ui.presenter.ChatSingleScreenPresenter;
import com.messenger.ui.presenter.ConversationListScreenPresenterImpl;
import com.messenger.ui.presenter.EditChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.NewChatScreenPresenterImpl;
import com.techery.spares.module.qualifier.ForApplication;
import com.messenger.ui.view.EditChatMembersScreenImpl;
import com.messenger.util.UnreadConversationObservable;
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

                LeaveChatDelegate.class,

                ChatSettingsScreenPresenterImpl.class,
                ConversationListScreenPresenterImpl.class,
                EditChatMembersScreenPresenterImpl.class,
                EditChatMembersScreenImpl.class,
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

    @Provides
    ChatDelegate provideChatDelegate(User user, MessengerServerFacade messengerServerFacade) {
        return new ChatDelegate(user, messengerServerFacade);
    }

    @Singleton
    @Provides
    UnreadConversationObservable provideUnreadConversationObservable(Context context) {
        return new UnreadConversationObservable(context);
    }

    @Singleton
    @Provides
    ActivityWatcher provideActivityWatcher (@ForApplication Context context){
        return new ActivityWatcher(context);
    }

}
