package com.messenger.ui.view.edit_member;

import com.messenger.flow.StyledPath;
import com.messenger.flow.container.Layout;
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
}
