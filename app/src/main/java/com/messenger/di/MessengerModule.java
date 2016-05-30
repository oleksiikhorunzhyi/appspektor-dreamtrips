package com.messenger.di;

import com.messenger.delegate.GroupChatEventDelegate;
import com.messenger.delegate.JoinedChatEventDelegate;
import com.messenger.delegate.LoaderDelegate;
import com.messenger.initializer.ChatFacadeInitializer;
import com.messenger.ui.adapter.SwipeableContactsAdapter;
import com.messenger.ui.adapter.holder.conversation.ClosedGroupConversationViewHolder;
import com.messenger.ui.adapter.holder.conversation.GroupConversationViewHolder;
import com.messenger.ui.adapter.holder.conversation.OneToOneConversationViewHolder;
import com.messenger.util.ChatFacadeManager;

import dagger.Module;

@Module(
        includes = {
                MessengerServerModule.class,
                MessengerStorageModule.class,
                MessengerDelegateModule.class
        },
        injects = {
                GroupConversationViewHolder.class,
                OneToOneConversationViewHolder.class,
                ClosedGroupConversationViewHolder.class,

                ChatFacadeInitializer.class,
                ChatFacadeManager.class,

                // adapters
                SwipeableContactsAdapter.class,

                LoaderDelegate.class,
                GroupChatEventDelegate.class,
                JoinedChatEventDelegate.class
        },
        complete = false, library = true
)
public class MessengerModule {


}
