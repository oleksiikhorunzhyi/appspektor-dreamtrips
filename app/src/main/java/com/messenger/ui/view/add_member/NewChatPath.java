package com.messenger.ui.view.add_member;


import com.worldventures.dreamtrips.core.flow.path.MasterDetailPath;
import com.worldventures.dreamtrips.core.flow.path.StyledPath;
import com.worldventures.dreamtrips.core.flow.util.Layout;
import com.messenger.ui.view.conversation.ConversationsPath;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.screen_new_chat)
public class NewChatPath extends StyledPath {
    @Override
    public MasterDetailPath getMaster() {
        return ConversationsPath.MASTER_PATH;
    }

}
