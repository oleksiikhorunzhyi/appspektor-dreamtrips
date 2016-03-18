package com.messenger.ui.view.add_member;

import com.messenger.flow.path.MasterDetailPath;
import com.messenger.flow.path.StyledPath;
import com.messenger.flow.util.Layout;
import com.messenger.ui.view.conversation.ConversationsPath;
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

    @Override
    public MasterDetailPath getMaster() {
        return ConversationsPath.MASTER_PATH;
    }

}
