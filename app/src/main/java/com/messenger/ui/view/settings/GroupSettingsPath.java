package com.messenger.ui.view.settings;

import com.messenger.ui.view.conversation.ConversationsPath;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;

@Layout(R.layout.screen_group_settings)
public class GroupSettingsPath extends StyledPath {

   private final String conversationId;

   public GroupSettingsPath(String conversationId) {
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
