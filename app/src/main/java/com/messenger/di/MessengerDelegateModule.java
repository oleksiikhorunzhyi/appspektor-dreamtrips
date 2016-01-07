package com.messenger.di;

import android.content.Context;

import com.messenger.delegate.ChatDelegate;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.messengerservers.entities.User;
import com.messenger.notification.UnhandledMessageWatcher;
import com.messenger.ui.inappnotifications.AppNotification;
import com.messenger.util.OpenedConversationTracker;
import com.messenger.util.UnreadConversationObservable;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
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
    OpenedConversationTracker providedOpenedConversationTracker() {
        return new OpenedConversationTracker();
    }

    @Provides
    UnhandledMessageWatcher provideUnhandledMessageWatcher(
            MessengerServerFacade messengerServerFacade,
            AppNotification appNotification,
            DreamSpiceManager spiceManager,
            OpenedConversationTracker openedConversationTracker) {
        return new UnhandledMessageWatcher(messengerServerFacade, appNotification, spiceManager, openedConversationTracker);
    }

}
