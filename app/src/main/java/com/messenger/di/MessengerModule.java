package com.messenger.di;

import com.messenger.delegate.ChatLeavingDelegate;
import com.messenger.initializer.ChatFacadeInitializer;
import com.messenger.service.MessengerNotificationPreSyncService;
import com.messenger.ui.activity.ChatActivity;
import com.messenger.ui.adapter.holder.CloseGroupConversationViewHolder;
import com.messenger.ui.adapter.holder.GroupConversationViewHolder;
import com.messenger.ui.adapter.holder.OneToOneConversationViewHolder;
import com.messenger.ui.adapter.holder.TripConversationViewHolder;
import com.messenger.ui.presenter.AddChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.ChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.ChatScreenPresenterImpl;
import com.messenger.ui.presenter.ConversationListScreenPresenterImpl;
import com.messenger.ui.presenter.EditChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.MultiChatSettingsScreenPresenter;
import com.messenger.ui.presenter.NewChatScreenPresenterImpl;
import com.messenger.ui.presenter.SingleChatSettingsScreenPresenterImpl;
import com.messenger.ui.view.ConversationListScreenImpl;
import com.messenger.ui.view.EditChatMembersScreenImpl;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.modules.messenger.MessengerContainerFragment;

import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                MessengerServerModule.class,
                MessengerStorageModule.class,
                MessengerDelegateModule.class
        },
        injects = {
                GroupConversationViewHolder.class,
                OneToOneConversationViewHolder.class,
                TripConversationViewHolder.class,
                CloseGroupConversationViewHolder.class,

                ChatFacadeInitializer.class,
                ChatLeavingDelegate.class,

//                presenters
                ChatScreenPresenterImpl.class,
                ChatMembersScreenPresenterImpl.class,
                NewChatScreenPresenterImpl.class,
                AddChatMembersScreenPresenterImpl.class,
                SingleChatSettingsScreenPresenterImpl.class,
                MultiChatSettingsScreenPresenter.class,
                ConversationListScreenPresenterImpl.class,
                EditChatMembersScreenPresenterImpl.class,

//                screen
                ConversationListScreenImpl.class,
                EditChatMembersScreenImpl.class,
                ChatActivity.class,

                MessengerNotificationPreSyncService.class,
        },
        complete = false, library = true
)
public class MessengerModule {

    public static final String MESSENGER = "Messenger";

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideMessengerComponent() {
        return new ComponentDescription(MESSENGER, R.string.messenger, R.string.messenger, R.drawable.ic_messenger,
                true, MessengerContainerFragment.class);
    }

}
