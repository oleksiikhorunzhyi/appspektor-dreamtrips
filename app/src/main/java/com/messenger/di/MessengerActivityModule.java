package com.messenger.di;

import android.app.Activity;

import com.messenger.delegate.CropImageDelegate;
import com.messenger.delegate.chat.ChatGroupCommandsInteractor;
import com.messenger.entities.DataUser;
import com.messenger.ui.adapter.ChatAdapter;
import com.messenger.ui.adapter.holder.chat.ChatHolderModule;
import com.messenger.ui.adapter.inflater.chat.ChatTimestampInflater;
import com.messenger.ui.module.flagging.FlaggingPresenterImpl;
import com.messenger.ui.presenter.AddChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.ChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.ChatScreenPresenterImpl;
import com.messenger.ui.presenter.ConversationListScreenPresenterImpl;
import com.messenger.ui.presenter.EditChatMembersScreenPresenterImpl;
import com.messenger.ui.presenter.MessengerActivityPresenter;
import com.messenger.ui.presenter.NewChatScreenPresenterImpl;
import com.messenger.ui.presenter.settings.GroupChatSettingsScreenPresenterImpl;
import com.messenger.ui.presenter.settings.SingleChatSettingsScreenPresenterImpl;
import com.messenger.ui.presenter.settings.TripChatScreenPresenterImpl;
import com.messenger.ui.util.avatar.MessengerMediaPickerDelegate;
import com.messenger.ui.util.avatar.MessengerMediaPickerDelegateImpl;
import com.messenger.ui.view.chat.ChatScreenImpl;
import com.messenger.ui.view.conversation.ConversationListScreenImpl;
import com.messenger.ui.view.edit_member.EditChatMembersScreenImpl;
import com.messenger.ui.view.settings.GroupChatSettingsScreenImpl;
import com.messenger.ui.view.settings.TripChatSettingsScreenImpl;
import com.messenger.ui.widget.MessengerPhotoPickerLayout;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.permission.PermissionDispatcher;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.delegate.DownloadFileInteractor;
import com.worldventures.dreamtrips.modules.common.service.MediaInteractor;
import com.worldventures.dreamtrips.modules.common.view.custom.PhotoPickerLayoutDelegate;
import com.worldventures.dreamtrips.modules.video.utils.CachedModelHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
      includes = {ChatHolderModule.class,},
      injects = {ConversationListScreenImpl.class, EditChatMembersScreenImpl.class, ChatScreenImpl.class, MessengerPhotoPickerLayout.class, GroupChatSettingsScreenImpl.class, TripChatSettingsScreenImpl.class,

            ChatGroupCommandsInteractor.class, ChatAdapter.class, ChatTimestampInflater.class,

            //presenters
            MessengerActivityPresenter.class, ChatScreenPresenterImpl.class, ChatMembersScreenPresenterImpl.class, NewChatScreenPresenterImpl.class, AddChatMembersScreenPresenterImpl.class,

            SingleChatSettingsScreenPresenterImpl.class, GroupChatSettingsScreenPresenterImpl.class, TripChatScreenPresenterImpl.class,

            ConversationListScreenPresenterImpl.class, EditChatMembersScreenPresenterImpl.class, FlaggingPresenterImpl.class,},
      complete = false, library = true)
public class MessengerActivityModule {
   public static final String MESSENGER = "Messenger";

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideMessengerComponent() {
      return new ComponentDescription(MESSENGER, R.string.messenger, R.string.messenger, R.drawable.ic_messenger, true, null);
   }

   @Provides
   DataUser provideUser(SessionHolder<UserSession> appSessionHolder) {
      return new DataUser(appSessionHolder.get().get().getUser().getUsername());
   }

   @Provides
   MessengerMediaPickerDelegate provideChangeAvatarDelegate(MediaInteractor mediaInteractor,
         PhotoPickerLayoutDelegate photoPickerLayoutDelegate,
         PermissionDispatcher permissionDispatcher) {
      return new MessengerMediaPickerDelegateImpl(mediaInteractor, photoPickerLayoutDelegate, permissionDispatcher);
   }

   @Provides
   @Singleton
   CropImageDelegate provideCropImageDelegate(Activity activity, DownloadFileInteractor downloadFileInteractor, CachedModelHelper cachedModelHelper) {
      return new CropImageDelegate(activity, downloadFileInteractor, cachedModelHelper);
   }

}
