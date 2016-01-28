package com.messenger.di;

import com.messenger.ui.view.conversation.ConversationListScreenImpl;
import com.messenger.ui.view.edit_member.EditChatMembersScreenImpl;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;

import dagger.Module;
import dagger.Provides;


@Module(
        injects = {
                ConversationListScreenImpl.class,
                EditChatMembersScreenImpl.class,
},
        complete = false, library = true
)public class MessengerActivityModule {
    public static final String MESSENGER = "Messenger";

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideMessengerComponent() {
        return new ComponentDescription(MESSENGER, R.string.messenger, R.string.messenger, R.drawable.ic_messenger,
                true, null);
    }
}
