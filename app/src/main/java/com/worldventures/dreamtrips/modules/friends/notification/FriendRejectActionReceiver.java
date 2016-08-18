package com.worldventures.dreamtrips.modules.friends.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.modules.friends.janet.ActOnFriendRequestCommand;
import com.worldventures.dreamtrips.modules.friends.janet.FriendsInteractor;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.modules.profile.bundle.UserBundle;

import javax.inject.Inject;

public class FriendRejectActionReceiver extends BroadcastReceiver {

   @Inject FriendsInteractor friendsInteractor;
   @Inject NotificationDelegate notifDelegate;

   @Override
   public void onReceive(Context context, Intent intent) {
      ((Injector) context.getApplicationContext()).inject(this);
      //
      UserBundle bundle = intent.getParcelableExtra(ComponentPresenter.EXTRA_DATA);
      friendsInteractor.rejectRequestPipe().send(new ActOnFriendRequestCommand.Reject(bundle.getUser()));
      notifDelegate.cancel(bundle.getUser().getId());
   }
}
