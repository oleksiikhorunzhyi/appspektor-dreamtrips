package com.messenger.di;

import android.content.Context;

import com.messenger.delegate.GroupChatEventDelegate;
import com.messenger.delegate.chat.ChatMessagesEventDelegate;
import com.messenger.initializer.ChatFacadeInitializer;
import com.messenger.messengerservers.MessengerServerFacade;
import com.messenger.notification.NotificationDataFactory;
import com.messenger.notification.UnhandledMessageWatcher;
import com.messenger.synchmechanism.MessengerConnector;
import com.messenger.synchmechanism.MessengerSyncDelegate;
import com.messenger.ui.adapter.SwipeableContactsAdapter;
import com.messenger.ui.adapter.holder.conversation.ClosedGroupConversationViewHolder;
import com.messenger.ui.adapter.holder.conversation.GroupConversationViewHolder;
import com.messenger.ui.adapter.holder.conversation.OneToOneConversationViewHolder;
import com.messenger.ui.inappnotifications.AppNotification;
import com.messenger.ui.inappnotifications.AppNotificationImpl;
import com.messenger.util.ChatFacadeManager;
import com.messenger.util.OpenedConversationTracker;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.App;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.util.ActivityWatcher;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      includes = {MessengerServerModule.class, MessengerStorageModule.class, MessengerDelegateModule.class, MessengerTypingManagerModule.class},
      injects = {GroupConversationViewHolder.class, OneToOneConversationViewHolder.class, ClosedGroupConversationViewHolder.class,

            ChatFacadeInitializer.class, ChatFacadeManager.class,

            // adapters
            SwipeableContactsAdapter.class,

            GroupChatEventDelegate.class,},
      complete = false, library = true)
public class MessengerModule {

   public static final String MESSENGER = "Messenger";

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideMessengerComponent() {
      return new ComponentDescription.Builder()
            .key(MESSENGER)
            .navMenuTitle(R.string.messenger)
            .toolbarTitle(R.string.messenger)
            .icon(R.drawable.ic_messenger)
            .skipGeneralToolbar(true)
            .shouldFinishMainActivity(true)
            .build();
   }

   @Provides
   @Singleton
   MessengerConnector messengerConnector(@ForApplication Context context, ActivityWatcher activityWatcher,
         SessionHolder appSessionHolder, MessengerServerFacade messengerServerFacade,
         MessengerSyncDelegate messengerSyncDelegate) {
      return new MessengerConnector(context, activityWatcher, appSessionHolder, messengerServerFacade, messengerSyncDelegate);
   }

   @Singleton
   @Provides
   ActivityWatcher provideActivityWatcher(Context context, SessionHolder sessionHolder) {
      return new ActivityWatcher(context, sessionHolder);
   }

   @Provides
   public AppNotification provideInAppNotification(App app) {
      return new AppNotificationImpl(app);
   }

   @Provides
   @Singleton
   public UnhandledMessageWatcher provideUnhandledMessageWatcher(AppNotification appNotification,
         ChatMessagesEventDelegate chatMessagesEventDelegate, OpenedConversationTracker openedConversationTracker,
         NotificationDataFactory notificationDataFactory) {
      return new UnhandledMessageWatcher(appNotification, chatMessagesEventDelegate, openedConversationTracker, notificationDataFactory);
   }
}
