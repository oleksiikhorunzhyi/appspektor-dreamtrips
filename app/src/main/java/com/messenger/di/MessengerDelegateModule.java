package com.messenger.di;

import android.content.Context;

import com.messenger.delegate.MessageBodyCreator;
import com.messenger.delegate.StartChatDelegate;
import com.messenger.delegate.chat.flagging.FlagMessageDelegate;
import com.messenger.delegate.conversation.helper.CreateConversationHelper;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.util.UserSectionHelper;
import com.messenger.util.ChatFacadeManager;
import com.messenger.util.OpenedConversationTracker;
import com.messenger.util.UnreadConversationObservable;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.LocaleHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

@Module(
      complete = false,
      library = true)
public class MessengerDelegateModule {
   @Provides
   ChatFacadeManager provideChatFacadeManager(@ForApplication Injector injector) {
      return new ChatFacadeManager(injector);
   }

   @Provides
   UserSectionHelper provideUserSectionHelper(@ForApplication Context context, SessionHolder<UserSession> appSessionHolder) {
      return new UserSectionHelper(context, appSessionHolder);
   }

   @Provides
   StartChatDelegate provideSingleChatDelegate(UsersDAO usersDAO, ParticipantsDAO participantsDAO, ConversationsDAO conversationsDAO, CreateConversationHelper createConversationHelper) {
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
   MessageBodyCreator provideMessageBodyCreator(LocaleHelper localeHelper) {
      return new MessageBodyCreator(localeHelper);
   }

   @Provides
   @Singleton
   FlagMessageDelegate provideFlagMessageDelegate(Janet janet) {
      return new FlagMessageDelegate(janet);
   }
}
