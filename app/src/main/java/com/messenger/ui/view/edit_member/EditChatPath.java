package com.messenger.ui.view.edit_member;

import com.messenger.ui.view.conversation.ConversationsPath;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_edit_chat_members)
public class EditChatPath extends StyledPath {

   private final String conversationId;

   public EditChatPath(String conversationId) {
      this.conversationId = conversationId;
   }

   public String getConversationId() {
      return conversationId;
   }

   @Override
   public MasterDetailPath getMaster() {
      return ConversationsPath.MASTER_PATH;
   }

}
