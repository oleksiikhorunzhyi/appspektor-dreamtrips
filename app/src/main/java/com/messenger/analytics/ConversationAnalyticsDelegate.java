package com.messenger.analytics;

import android.text.TextUtils;

import com.innahema.collections.query.queriables.Queryable;
import com.messenger.entities.DataConversation;
import com.messenger.entities.DataUser;
import com.messenger.messengerservers.constant.ConversationType;
import com.messenger.ui.helper.ConversationHelper;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.worldventures.core.service.analytics.AnalyticsInteractor;

import java.util.List;

import javax.inject.Inject;

public class ConversationAnalyticsDelegate {

   private AnalyticsInteractor analyticsInteractor;

   @Inject
   public ConversationAnalyticsDelegate(AnalyticsInteractor analyticsInteractor) {
      this.analyticsInteractor = analyticsInteractor;
   }

   public void trackOpenedConversation(@NotNull DataConversation openedConversation, @NotNull List<DataUser> participants) {
      AnalyticsType analyticsType = obtainAnalyticsType(openedConversation, participants);
      String conversationSubject = ConversationHelper.obtainConversationSubject(openedConversation, participants);
      int count = participants.size();
      analyticsInteractor.analyticsActionPipe().send(getAnalyticsAction(analyticsType, conversationSubject, count));
   }

   public OpenConversationAction getAnalyticsAction(AnalyticsType analyticsType, String conversationSubject, int count) {
      switch (analyticsType) {
         case CHAT:
            return OpenConversationAction.forSingleConversation();
         case CHAT_WITH_HOST:
            return OpenConversationAction.forSingleInDestinationConversation();
         case GROUP_CHAT:
            return OpenConversationAction.forGroupConversation(conversationSubject, count);
         case TRIP_CHAT:
            return OpenConversationAction.forTripConversation(conversationSubject, count);
         case TRIP_CHAT_WITH_HOST:
            return OpenConversationAction.forTripWithHostConversation(conversationSubject, count);
         default:
            throw new RuntimeException("Unknown type");
      }
   }

   private AnalyticsType obtainAnalyticsType(DataConversation conversation, List<DataUser> participants) {
      boolean chatWithHost = Queryable.from(participants).firstOrDefault(DataUser::isHost) != null;

      if (TextUtils.equals(conversation.getType(), ConversationType.CHAT)) {
         return chatWithHost ? AnalyticsType.CHAT_WITH_HOST : AnalyticsType.CHAT;
      } else {
         return obtainGroupAnalyticsType(conversation, chatWithHost);
      }
   }

   private AnalyticsType obtainGroupAnalyticsType(DataConversation conversation, boolean chatWithHost) {
      boolean isTripChat = ConversationHelper.isTripChat(conversation);
      if (isTripChat) {
         return chatWithHost ? AnalyticsType.TRIP_CHAT_WITH_HOST : AnalyticsType.TRIP_CHAT;
      } else return AnalyticsType.GROUP_CHAT;
   }
}
