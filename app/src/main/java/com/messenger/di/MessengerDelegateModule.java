package com.messenger.di;

import android.content.Context;

import com.messenger.delegate.AttachmentDelegate;
import com.messenger.delegate.ChatDelegate;
import com.messenger.delegate.ConversationAvatarDelegate;
import com.messenger.delegate.CropImageDelegate;
import com.messenger.delegate.MessageBodyCreator;
import com.messenger.delegate.MessageTranslationDelegate;
import com.messenger.delegate.PaginationDelegate;
import com.messenger.delegate.StartChatDelegate;
import com.messenger.delegate.UserProcessor;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.notification.UnhandledMessageWatcher;
import com.messenger.storage.dao.AttachmentDAO;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.MessageDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.TranslationsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.helper.PhotoPickerDelegate;
import com.messenger.ui.inappnotifications.AppNotification;
import com.messenger.ui.util.UserSectionHelper;
import com.messenger.util.ChatFacadeManager;
import com.messenger.util.OpenedConversationTracker;
import com.messenger.util.UnreadConversationObservable;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.module.qualifier.Global;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

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
    UserProcessor provideUserProcessor (@ForApplication Context context, DreamSpiceManager requester, UsersDAO usersDAO) {
        requester.start(context);
        return new UserProcessor(usersDAO, requester);
    }

    @Provides
    ChatFacadeManager provideChatFacadeManager(@ForApplication Injector injector) {
        return new ChatFacadeManager(injector);
    }

    @Provides
    UserSectionHelper provideUserSectionHelper(@ForApplication  Context context, DataUser user) {
        return new UserSectionHelper(context, user);
    }

    @Provides
    PaginationDelegate providePaginationDelegate(MessengerServerFacade messengerServerFacade, MessageDAO messageDAO, AttachmentDAO attachmentDAO) {
        return new PaginationDelegate(messengerServerFacade, messageDAO, attachmentDAO);
    }

    @Singleton
    @Provides
    MessageTranslationDelegate provideMessageTranslationDelegate(@ForApplication Context context, DreamSpiceManager dreamSpiceManager, TranslationsDAO translationsDAO, LocaleHelper localeHelper){
        return new MessageTranslationDelegate(context, dreamSpiceManager, translationsDAO, localeHelper);
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
    AttachmentDelegate provideAttachmentDelegate(PhotoUploadingManagerS3 photoUploadingManager, MessageDAO messageDAO, AttachmentDAO attachmentDAO) {
        return new AttachmentDelegate(photoUploadingManager, messageDAO, attachmentDAO);
    }

    @Provides
    MessageBodyCreator provideMessageBodyCreator(LocaleHelper localeHelper, SessionHolder<UserSession> userSessionHolder) {
        return new MessageBodyCreator(localeHelper, userSessionHolder.get().get().getUser());
    }

    @Provides
    PhotoPickerDelegate providePhotoPickerDelegate(@Global EventBus eventBus) {
        return new PhotoPickerDelegate(eventBus);
    }

    @Provides
    @Singleton
    ConversationAvatarDelegate provideConversationAvatarDelegate(PhotoUploadingManagerS3 photoUploadingManager, MessengerServerFacade messengerServerFacade, ConversationsDAO conversationsDAO) {
        return new ConversationAvatarDelegate(photoUploadingManager, messengerServerFacade, conversationsDAO);
    }
}
