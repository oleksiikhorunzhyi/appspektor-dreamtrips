package com.messenger.di;

import android.content.Context;

import com.messenger.delegate.GroupChatEventDelegate;
import com.messenger.delegate.MessageBodyCreator;
import com.messenger.delegate.StartChatDelegate;
import com.messenger.delegate.chat.ChatMessagesEventDelegate;
import com.messenger.delegate.chat.event.ChatEventInteractor;
import com.messenger.delegate.chat.flagging.FlagMessageDelegate;
import com.messenger.delegate.conversation.helper.CreateConversationHelper;
import com.messenger.delegate.user.JoinedChatEventDelegate;
import com.messenger.storage.dao.ConversationsDAO;
import com.messenger.storage.dao.ParticipantsDAO;
import com.messenger.storage.dao.UsersDAO;
import com.messenger.ui.util.UserSectionHelper;
import com.messenger.util.ChatFacadeManager;
import com.messenger.util.OpenedConversationTracker;
import com.messenger.util.UnreadConversationObservable;
import com.worldventures.core.model.session.SessionHolder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.techery.janet.Janet;

@Module(complete = false, library = true)
public class MessengerDelegateModule {

   @Provides
   ChatFacadeManager provideChatFacadeManager(ChatMessagesEventDelegate chatMessagesDelegate, GroupChatEventDelegate groupChatEventDelegate,
         JoinedChatEventDelegate joinedChatDelegate, ChatEventInteractor chatEventInteractor) {
      return new ChatFacadeManager(chatMessagesDelegate, groupChatEventDelegate, joinedChatDelegate, chatEventInteractor);
   }

   @Provides
   UserSectionHelper provideUserSectionHelper(Context context, SessionHolder appSessionHolder) {
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
   MessageBodyCreator provideMessageBodyCreator() {
      return new MessageBodyCreator();
   }

   @Provides
   @Singleton
   FlagMessageDelegate provideFlagMessageDelegate(Janet janet) {
      return new FlagMessageDelegate(janet);
   }
}
