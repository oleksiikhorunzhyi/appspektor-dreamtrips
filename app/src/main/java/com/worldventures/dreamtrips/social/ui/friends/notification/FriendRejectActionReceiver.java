package com.worldventures.dreamtrips.social.ui.friends.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.social.service.users.base.interactor.FriendsInteractor;
import com.worldventures.dreamtrips.social.service.users.request.command.ActOnFriendRequestCommand;
import com.worldventures.dreamtrips.social.ui.activity.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.social.ui.profile.bundle.UserBundle;

import javax.inject.Inject;

public class FriendRejectActionReceiver extends BroadcastReceiver {

   @Inject FriendsInteractor friendsInteractor;
   @Inject NotificationDelegate notifDelegate;

   @Override
   public void onReceive(Context context, Intent intent) {
      ((Injector) context.getApplicationContext()).inject(this);
      //
      UserBundle bundle = intent.getParcelableExtra(ComponentPresenter.EXTRA_DATA);
      friendsInteractor.getRejectRequestPipe().send(new ActOnFriendRequestCommand.Reject(bundle.getUser()));
      notifDelegate.cancel(bundle.getUser().getId());
   }
}
