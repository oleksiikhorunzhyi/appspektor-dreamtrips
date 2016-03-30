package com.messenger.ui.view.edit_member;

import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.messenger.ui.view.conversation.ConversationsPath;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.screen_edit_chat_members)
public class EditChatPath extends StyledPath {

    private String conversationId;

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
