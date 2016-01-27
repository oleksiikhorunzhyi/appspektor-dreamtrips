package com.messenger.ui.view.conversation;

import com.messenger.flow.StyledPath;
import com.messenger.flow.container.Layout;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.screen_conversation_list)
public class ConversationsPath extends StyledPath {

    @Override
    public PathAttrs getAttrs() {
        return WITH_DRAWER;
    }
}
