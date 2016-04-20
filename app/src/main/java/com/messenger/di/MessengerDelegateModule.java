package com.messenger.di;

import android.content.Context;

import com.messenger.delegate.ChatMessagesEventDelegate;
import com.messenger.delegate.CreateConversationHelper;
import com.messenger.delegate.MessageBodyCreator;
import com.messenger.delegate.MessageTranslationDelegate;
import com.messenger.delegate.StartChatDelegate;
import com.messenger.delegate.UserProcessor;
import com.messenger.notification.UnhandledMessageWatcher;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.TranslationsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.helper.LegacyPhotoPickerDelegate;
import com.messenger.ui.inappnotifications.AppNotification;
import com.messenger.ui.util.UserSectionHelper;
import com.messenger.util.ChatFacadeManager;
import com.messenger.util.OpenedConversationTracker;
import com.messenger.util.UnreadConversationObservable;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import io.techery.janet.Janet;

@Module(
        complete = false,
        library = true
)
public class MessengerDelegateModule {
    @Provides
    UserProcessor provideUserProcessor(Janet janet, UsersDAO usersDAO) {
        return new UserProcessor(usersDAO, janet);
    }

    @Provides
    ChatFacadeManager provideChatFacadeManager(@ForApplication Injector injector) {
        return new ChatFacadeManager(injector);
    }

    @Provides
    UserSectionHelper provideUserSectionHelper(@ForApplication Context context, SessionHolder<UserSession> appSessionHolder) {
        return new UserSectionHelper(context, appSessionHolder);
    }

    @Singleton
    @Provides
    MessageTranslationDelegate provideMessageTranslationDelegate(Janet janet, TranslationsDAO translationsDAO, LocaleHelper localeHelper) {
        return new MessageTranslationDelegate(janet, translationsDAO, localeHelper);
    }

    @Provides
    StartChatDelegate provideSingleChatDelegate(UsersDAO usersDAO, ParticipantsDAO participantsDAO,
                                                ConversationsDAO conversationsDAO, CreateConversationHelper createConversationHelper) {
        return new StartChatDelegate(usersDAO, participantsDAO, conversationsDAO, createConversationHelper);
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
            AppNotification appNotification,
            ChatMessagesEventDelegate chatMessagesEventDelegate,
            ConversationsDAO conversationsDAO,
            UsersDAO usersDAO,
            ParticipantsDAO participantsDAO,
            AttachmentDAO attachmentDAO,
            OpenedConversationTracker openedConversationTracker) {
        return new UnhandledMessageWatcher(appNotification, chatMessagesEventDelegate, openedConversationTracker, conversationsDAO, participantsDAO, usersDAO, attachmentDAO);
    }

    @Provides
    LegacyPhotoPickerDelegate providePhotoPickerDelegate(@Global EventBus eventBus) {
        return new LegacyPhotoPickerDelegate(eventBus);
    }

    @Provides
    MessageBodyCreator provideMessageBodyCreator(LocaleHelper localeHelper, SessionHolder<UserSession> userSessionHolder) {
        return new MessageBodyCreator(localeHelper, userSessionHolder.get().get().getUser());
    }

    @Provides
    @Singleton
    ChatMessagesEventDelegate provideChatMessagesEventDelegate(@ForApplication Injector injector) {
        return new ChatMessagesEventDelegate(injector);
    }
}
