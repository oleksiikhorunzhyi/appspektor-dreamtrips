package com.messenger.ui.view.settings;

import com.messenger.flow.StyledPath;
import com.messenger.flow.container.Layout;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.screen_single_settings)
public class SingleSettingsPath extends StyledPath {
    private String conversationId;

    public SingleSettingsPath(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationId() {
        return conversationId;
    }
}
