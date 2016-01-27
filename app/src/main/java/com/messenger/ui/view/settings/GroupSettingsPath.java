package com.messenger.ui.view.settings;

import com.messenger.flow.StyledPath;
import com.messenger.flow.container.Layout;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.screen_group_settings)
public class GroupSettingsPath extends StyledPath {
    private String conversationId;

    public GroupSettingsPath(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationId() {
        return conversationId;
    }
}
