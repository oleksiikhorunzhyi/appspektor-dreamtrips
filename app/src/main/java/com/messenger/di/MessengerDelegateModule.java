package com.messenger.di;

import com.messenger.delegate.ChatDelegate;
import com.messenger.delegate.StartChatDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.User;
import com.messenger.notification.UnhandledMessageWatcher;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.inappnotifications.AppNotification;
import com.messenger.util.OpenedConversationTracker;
import com.messenger.util.RxContentResolver;
import com.messenger.util.UnreadConversationObservable;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class MessengerDelegateModule {

    @Provides
    User provideUser(SessionHolder<UserSession> appSessionHolder) {
        return new User(appSessionHolder.get().get().getUser().getUsername());
    }

    @Provides
    ChatDelegate provideChatDelegate(User user, MessengerServerFacade messengerServerFacade) {
        return new ChatDelegate(user, messengerServerFacade);
    }

    @Provides
    StartChatDelegate provideStartSingleChatDelegate(UsersDAO usersDAO, ParticipantsDAO participantsDAO,
                                                           ConversationsDAO conversationsDAO, ChatDelegate chatDelegate){
        return new StartChatDelegate(usersDAO, participantsDAO, conversationsDAO, chatDelegate);
    }

    @Singleton
    @Provides
    UnreadConversationObservable provideUnreadConversationObservable(@Named(MessengerStorageModule.DB_FLOW_RX_RESOLVER) RxContentResolver resolver) {
        return new UnreadConversationObservable(resolver);
    }

    @Singleton
    @Provides
    OpenedConversationTracker providedOpenedConversationTracker() {
        return new OpenedConversationTracker();
    }

    @Provides
    UnhandledMessageWatcher provideUnhandledMessageWatcher(
            MessengerServerFacade messengerServerFacade,
            AppNotification appNotification,
            DreamSpiceManager spiceManager,
            ConversationsDAO conversationsDAO,
            UsersDAO usersDAO,
            ParticipantsDAO participantsDAO,
            OpenedConversationTracker openedConversationTracker) {
        return new UnhandledMessageWatcher(messengerServerFacade, appNotification, spiceManager, openedConversationTracker,  conversationsDAO, participantsDAO, usersDAO);
    }

}
