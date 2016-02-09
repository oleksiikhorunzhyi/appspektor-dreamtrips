package com.messenger.di;

import com.messenger.delegate.AttachmentDelegate;
import com.messenger.delegate.ChatDelegate;
import com.messenger.delegate.StartChatDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.entities.DataUser;
import com.messenger.notification.UnhandledMessageWatcher;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.inappnotifications.AppNotification;
import com.messenger.util.OpenedConversationTracker;
import com.messenger.util.UnreadConversationObservable;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManager;
import com.worldventures.dreamtrips.core.session.UserSession;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class MessengerDelegateModule {

    @Provides
    DataUser provideUser(SessionHolder<UserSession> appSessionHolder) {
        return new DataUser(appSessionHolder.get().get().getUser().getUsername());
    }

    @Provides
    ChatDelegate provideChatDelegate(DataUser user, MessengerServerFacade messengerServerFacade) {
        return new ChatDelegate(user.getId(), messengerServerFacade);
    }

    @Provides
    StartChatDelegate provideSingleChatDelegate(UsersDAO usersDAO, ParticipantsDAO participantsDAO,
                                                           ConversationsDAO conversationsDAO, ChatDelegate chatDelegate){
        return new StartChatDelegate(usersDAO, participantsDAO, conversationsDAO, chatDelegate);
    }

    @Singleton
    @Provides
    UnreadConversationObservable provideUnreadConversationObservable(ConversationsDAO conversationsDAO) {
        return new UnreadConversationObservable(conversationsDAO);
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
            AttachmentDAO attachmentDAO,
            OpenedConversationTracker openedConversationTracker) {
        return new UnhandledMessageWatcher(messengerServerFacade, appNotification, spiceManager, openedConversationTracker,  conversationsDAO, participantsDAO, usersDAO, attachmentDAO);
    }

    @Provides
    AttachmentDelegate provideAttachmentDelegate(PhotoUploadingManager photoUploadingManager, MessageDAO messageDAO, AttachmentDAO attachmentDAO) {
        return new AttachmentDelegate(photoUploadingManager, messageDAO, attachmentDAO);
    }
}
