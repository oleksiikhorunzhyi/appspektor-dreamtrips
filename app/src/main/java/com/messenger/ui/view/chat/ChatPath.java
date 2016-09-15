package com.messenger.ui.view.chat;

import com.messenger.ui.view.conversation.ConversationsPath;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_chat)
public class ChatPath extends StyledPath {

   private String conversationId;

   public ChatPath(String conversationId) {
      this.conversationId = conversationId;
   }

   public String getConversationId() {
      return conversationId;
   }

   @Override
   public MasterDetailPath getMaster() {
      return ConversationsPath.MASTER_PATH;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ChatPath chatPath = (ChatPath) o;

      return !(conversationId != null ? !conversationId.equals(chatPath.conversationId) : chatPath.conversationId != null);

   }

   @Override
   public int hashCode() {
      return conversationId != null ? conversationId.hashCode() : 0;
   }
}
