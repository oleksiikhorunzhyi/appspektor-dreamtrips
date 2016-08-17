package com.messenger.ui.presenter.settings;

import android.content.Context;
import android.text.TextUtils;
import android.view.Menu;

import com.messenger.delegate.ProfileCrosser;
import com.messenger.ui.view.settings.ChatSettingsScreen;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;

import javax.inject.Inject;

import rx.Observable;

public class SingleChatSettingsScreenPresenterImpl extends BaseChatSettingsScreenPresenterImpl<ChatSettingsScreen> implements SingleChatSettingsScreenPresenter {

   @Inject ProfileCrosser profileCrosser;

   public SingleChatSettingsScreenPresenterImpl(Context context, Injector injector, String conversationId) {
      super(context, injector, conversationId);
   }

   @Override
   public void onConversationAvatarClick() {
      participantsObservable.flatMap(Observable::from)
            .filter(participant -> !TextUtils.equals(currentUser.getId(), participant.getId()))
            .take(1)
            .subscribe(profileCrosser::crossToProfile);
   }

   @Override
   public void onToolbarMenuPrepared(Menu menu) {
      super.onToolbarMenuPrepared(menu);
      menu.findItem(R.id.action_overflow).setVisible(false);
   }
}
