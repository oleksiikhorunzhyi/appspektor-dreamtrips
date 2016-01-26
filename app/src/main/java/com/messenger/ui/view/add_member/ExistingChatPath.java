package com.messenger.ui.view.add_member;

import com.messenger.flow.StyledPath;
import com.messenger.flow.container.Layout;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.screen_exisiting_chat)
public class ExistingChatPath extends StyledPath {

    private String conversationId;

    public ExistingChatPath(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationId() {
        return conversationId;
    }
}
