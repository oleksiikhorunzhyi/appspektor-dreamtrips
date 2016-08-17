package com.messenger.messengerservers;

import android.support.annotation.NonNull;

import com.messenger.messengerservers.constant.ConversationType;

public class ConversationIdHelper {

   @ConversationType.Type
   public String obtainType(@NonNull String conversationId, @NonNull String yourId) {
      if (conversationId.contains("dreamtrip_auto_gen")) {
         return ConversationType.TRIP;
      } else if (conversationId.contains(yourId)) {
         return ConversationType.CHAT;
      } else {
         return ConversationType.GROUP;
      }
   }
}
