package com.messenger.di;


import android.app.Activity;

import com.messenger.delegate.ChatLeavingDelegate;
import com.messenger.delegate.CropImageDelegate;
import com.messenger.entities.DataUser;
import com.messenger.ui.helper.PhotoPickerDelegate;
import com.messenger.ui.presenter.AddChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.ChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.ChatScreenPresenterImpl;
import com.messenger.ui.presenter.ConversationListScreenPresenterImpl;
import com.messenger.ui.presenter.EditChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.MessengerActivityPresenter;
import com.messenger.ui.presenter.MultiChatSettingsScreenPresenter;
import com.messenger.ui.presenter.NewChatScreenPresenterImpl;
import com.messenger.ui.presenter.SingleChatSettingsScreenPresenterImpl;
import com.messenger.ui.util.avatar.ChangeAvatarDelegate;
import com.messenger.ui.util.avatar.ChangeAvatarDelegateImpl;
import com.messenger.ui.view.chat.ChatScreenImpl;
import com.messenger.ui.view.conversation.ConversationListScreenImpl;
import com.messenger.ui.view.edit_member.EditChatMembersScreenImpl;
import com.messenger.ui.view.settings.GroupChatSettingsScreenImpl;
import com.messenger.ui.view.settings.TripChatSettingsScreenImpl;
import com.messenger.ui.widget.MessengerPhotoPickerLayout;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayoutDelegate;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module(
        injects = {
                ConversationListScreenImpl.class,
                EditChatMembersScreenImpl.class,
                ChatScreenImpl.class,
                MessengerPhotoPickerLayout.class,
                GroupChatSettingsScreenImpl.class,
                TripChatSettingsScreenImpl.class,

                ChatLeavingDelegate.class,

                //presenters
                MessengerActivityPresenter.class,
                ChatScreenPresenterImpl.class,
                ChatMembersScreenPresenterImpl.class,
                NewChatScreenPresenterImpl.class,
                AddChatMembersScreenPresenterImpl.class,
                SingleChatSettingsScreenPresenterImpl.class,
                MultiChatSettingsScreenPresenter.class,
                ConversationListScreenPresenterImpl.class,
                EditChatMembersScreenPresenterImpl.class,
},
        complete = false, library = true
)public class MessengerActivityModule {
    public static final String MESSENGER = "Messenger";

    @Provides(type = Provides.Type.SET)
    ComponentDescription provideMessengerComponent() {
        return new ComponentDescription(MESSENGER, R.string.messenger, R.string.messenger, R.drawable.ic_messenger,
                true, null);
    }

    @Provides
    DataUser provideUser(SessionHolder<UserSession> appSessionHolder) {
        return new DataUser(appSessionHolder.get().get().getUser().getUsername());
    }

    @Provides
    ChangeAvatarDelegate provideChangeAvatarDelegate(PhotoPickerDelegate photoPickerDelegate, PhotoPickerLayoutDelegate photoPickerLayoutDelegate) {
        return new ChangeAvatarDelegateImpl(photoPickerDelegate, photoPickerLayoutDelegate);
    }

    @Provides
    @Singleton
    CropImageDelegate provideCropImageDelegate(Activity activity, DreamSpiceManager dreamSpiceManager) {
        return new CropImageDelegate(activity, dreamSpiceManager);
    }
}
