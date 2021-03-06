package com.messenger.messengerservers.xmpp.providers;

import com.google.gson.Gson;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.xmpp.stanzas.incoming.ConversationListIQ;

import java.util.List;

public class ConversationListProvider extends BaseConversationProvider<ConversationListIQ> {

   public ConversationListProvider(Gson gson) {
      super(gson);
   }

   @Override
   protected ConversationListIQ constructIQ(List<Conversation> data) {
      return new ConversationListIQ(data);
   }

   @Override
   protected String getEndElement() {
      return "list";
   }
}
