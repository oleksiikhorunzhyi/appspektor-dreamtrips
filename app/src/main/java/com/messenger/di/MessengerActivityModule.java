package com.messenger.di;


import android.app.Activity;

import com.messenger.delegate.CropImageDelegate;
import com.messenger.ui.helper.PhotoPickerDelegate;
import com.messenger.ui.presenter.ChatScreenPresenterImpl;
import com.messenger.ui.presenter.MessengerActivityPresenter;
import com.messenger.ui.presenter.MultiChatSettingsScreenPresenter;
import com.messenger.ui.util.avatar.ChangeAvatarDelegate;
import com.messenger.ui.util.avatar.ChangeAvatarDelegateImpl;
import com.messenger.ui.view.chat.ChatScreenImpl;
import com.messenger.ui.view.conversation.ConversationListScreenImpl;
import com.messenger.ui.view.edit_member.EditChatMembersScreenImpl;
import com.messenger.ui.view.settings.GroupChatSettingsScreenImpl;
import com.messenger.ui.view.settings.TripChatSettingsScreenImpl;
import com.messenger.ui.widget.MessengerPhotoPickerLayout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayoutDelegate;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module(
        injects = {
                ConversationListScreenImpl.class,
                EditChatMembersScreenImpl.class,
                ChatScreenImpl.class,
                ChatScreenPresenterImpl.class,
                MessengerActivityPresenter.class,
                MessengerPhotoPickerLayout.class,
                GroupChatSettingsScreenImpl.class,
                TripChatSettingsScreenImpl.class,
                MultiChatSettingsScreenPresenter.class,
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
    ChangeAvatarDelegate provideChangeAvatarDelegate(PhotoPickerDelegate photoPickerDelegate, PhotoPickerLayoutDelegate photoPickerLayoutDelegate) {
        return new ChangeAvatarDelegateImpl(photoPickerDelegate, photoPickerLayoutDelegate);
    }

    @Provides
    @Singleton
    CropImageDelegate provideCropImageDelegate(Activity activity, DreamSpiceManager dreamSpiceManager) {
        return new CropImageDelegate(activity, dreamSpiceManager);
    }
}
