package com.messenger.di;

import com.messenger.delegate.ChatLeavingDelegate;
import com.messenger.initializer.ChatFacadeInitializer;
import com.messenger.service.MessengerNotificationPreSyncService;
import com.messenger.ui.adapter.SwipeableContactsAdapter;
import com.messenger.ui.adapter.holder.CloseGroupConversationViewHolder;
import com.messenger.ui.adapter.holder.GroupConversationViewHolder;
import com.messenger.ui.adapter.holder.OneToOneConversationViewHolder;
import com.messenger.ui.adapter.holder.TripConversationViewHolder;
import com.messenger.ui.helper.PhotoPickerDelegate;
import com.messenger.ui.presenter.AddChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.ChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.ConversationListScreenPresenterImpl;
import com.messenger.ui.presenter.EditChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.MultiChatSettingsScreenPresenter;
import com.messenger.ui.presenter.NewChatScreenPresenterImpl;
import com.messenger.ui.presenter.SingleChatSettingsScreenPresenterImpl;

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
                TripConversationViewHolder.class,
                CloseGroupConversationViewHolder.class,

                ChatFacadeInitializer.class,

                // adapters
                SwipeableContactsAdapter.class,

                PhotoPickerDelegate.class,

                MessengerNotificationPreSyncService.class,
        },
        complete = false, library = true
)
public class MessengerModule {


}
