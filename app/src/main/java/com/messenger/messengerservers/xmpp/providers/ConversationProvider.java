package com.messenger.messengerservers.xmpp.providers;

import com.google.gson.Gson;
import com.messenger.messengerservers.model.Conversation;
import com.messenger.messengerservers.xmpp.stanzas.incoming.ConversationIQ;

import java.util.List;

public class ConversationProvider extends BaseConversationProvider<ConversationIQ> {

   public ConversationProvider(Gson gson) {
      super(gson);
   }

   @Override
   protected ConversationIQ constructIQ(List<Conversation> data) {
      ConversationIQ conversationIQ = new ConversationIQ();
      if (!data.isEmpty()) {
         conversationIQ.setConversation(data.get(0));
      }
      return conversationIQ;
   }

   @Override
   protected String getEndElement() {
      return ConversationIQ.ELEMENT;
   }
}
