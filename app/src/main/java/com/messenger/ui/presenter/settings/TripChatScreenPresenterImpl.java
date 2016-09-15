package com.messenger.ui.presenter.settings;

import android.content.Context;
import android.view.Menu;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.R;

public class TripChatScreenPresenterImpl extends BaseGroupChatSettingsScreenPresenterImpl {

   public TripChatScreenPresenterImpl(Context context, Injector injector, String conversationId) {
      super(context, injector, conversationId);
   }

   @Override
   public void onToolbarMenuPrepared(Menu menu) {
      super.onToolbarMenuPrepared(menu);
      menu.findItem(R.id.action_overflow).setVisible(false);
   }
}
