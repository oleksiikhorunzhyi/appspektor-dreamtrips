package com.messenger.di;

import android.app.Activity;
import android.content.Context;

import com.messenger.delegate.CropImageDelegate;
import com.messenger.delegate.chat.ChatGroupCommandsInteractor;
import com.messenger.entities.DataUser;
import com.messenger.storage.MessengerDatabase;
import com.messenger.synchmechanism.MessengerConnector;
import com.messenger.ui.activity.MessengerActivity;
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
import com.messenger.ui.presenter.ToolbarPresenter;
import com.messenger.ui.presenter.settings.GroupChatSettingsScreenPresenterImpl;
import com.messenger.ui.presenter.settings.SingleChatSettingsScreenPresenterImpl;
import com.messenger.ui.presenter.settings.TripChatScreenPresenterImpl;
import com.messenger.ui.view.chat.ChatScreenImpl;
import com.messenger.ui.view.conversation.ConversationListScreenImpl;
import com.messenger.ui.view.edit_member.EditChatMembersScreenImpl;
import com.messenger.ui.view.settings.GroupChatSettingsScreenImpl;
import com.messenger.ui.view.settings.TripChatSettingsScreenImpl;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.modules.auth.api.command.LogoutAction;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

@Module(
      includes = {ChatHolderModule.class},
      injects = {
            MessengerActivity.class,

            ConversationListScreenImpl.class,
            EditChatMembersScreenImpl.class,
            ChatScreenImpl.class,
            GroupChatSettingsScreenImpl.class,
            TripChatSettingsScreenImpl.class,
            ChatGroupCommandsInteractor.class,
            ChatAdapter.class,
            ChatTimestampInflater.class,

            //presenters
            MessengerActivityPresenter.class,
            ChatScreenPresenterImpl.class,
            ChatMembersScreenPresenterImpl.class,
            NewChatScreenPresenterImpl.class,
            AddChatMembersScreenPresenterImpl.class,
            SingleChatSettingsScreenPresenterImpl.class,
            GroupChatSettingsScreenPresenterImpl.class,
            TripChatScreenPresenterImpl.class,
            ConversationListScreenPresenterImpl.class,
            EditChatMembersScreenPresenterImpl.class,
            FlaggingPresenterImpl.class,
            ToolbarPresenter.class,
      },
      complete = false, library = true)
public class MessengerActivityModule {

   @Provides
   DataUser provideUser(SessionHolder appSessionHolder) {
      return new DataUser(appSessionHolder.get().get().user().getUsername());
   }

   @Provides
   @Singleton
   CropImageDelegate provideCropImageDelegate(Activity activity) {
      return new CropImageDelegate(activity);
   }

   @Provides(type = Provides.Type.SET)
   LogoutAction messengerDisconnectLogoutAction(MessengerConnector messengerConnector, Context context) {
      return () -> {
         messengerConnector.disconnect();
         try {
            FlowManager.getDatabase(MessengerDatabase.NAME).reset(context);
         } catch (Throwable e) {
            Timber.w(e, "Messenger DB is not cleared");
         }
      };
   }

}
