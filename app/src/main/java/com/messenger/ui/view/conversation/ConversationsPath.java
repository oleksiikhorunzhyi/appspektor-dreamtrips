package com.messenger.ui.view.conversation;

import com.messenger.flow.path.MasterDetailPath;
import com.messenger.flow.path.StyledPath;
import com.messenger.flow.util.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.flow.path.PathAttrs;

@Layout(R.layout.screen_conversation_list)
public class ConversationsPath extends StyledPath {

    /**
     * don't create instance of this path, use static instance instead provided below
     */
    private ConversationsPath() {
    }

    @Override
    public PathAttrs getAttrs() {
        return PathAttrs.WITH_DRAWER;
    }

    @Override
    public MasterDetailPath getMaster() {
        return MASTER_PATH;
    }

    public static final ConversationsPath MASTER_PATH = new ConversationsPath();
}
