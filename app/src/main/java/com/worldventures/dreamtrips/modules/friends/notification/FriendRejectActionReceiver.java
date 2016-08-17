package com.worldventures.dreamtrips.modules.friends.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.friends.api.ActOnRequestCommand;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import org.json.JSONObject;

import javax.inject.Inject;

import static com.worldventures.dreamtrips.modules.friends.api.ActOnRequestCommand.Action.REJECT;

public class FriendRejectActionReceiver extends BroadcastReceiver {

   @Inject DreamSpiceManager dreamSpiceManager;
   @Inject NotificationDelegate notifDelegate;

   @Override
   public void onReceive(Context context, Intent intent) {
      ((Injector) context.getApplicationContext()).inject(this);
      //
      UserBundle bundle = intent.getParcelableExtra(ComponentPresenter.EXTRA_DATA);
      dreamSpiceManager.start(context);
      dreamSpiceManager.execute(new ActOnRequestCommand(bundle.getUser()
            .getId(), REJECT.name()), new RequestListener<JSONObject>() {
         @Override
         public void onRequestFailure(SpiceException spiceException) {
            dreamSpiceManager.shouldStop();
         }

         @Override
         public void onRequestSuccess(JSONObject jsonObject) {
            dreamSpiceManager.shouldStop();
         }
      });
      //
      notifDelegate.cancel(bundle.getUser().getId());
   }
}
