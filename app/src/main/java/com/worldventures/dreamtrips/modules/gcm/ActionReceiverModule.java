package com.worldventures.dreamtrips.modules.gcm;

import com.worldventures.dreamtrips.social.ui.friends.notification.FriendRejectActionReceiver;

import dagger.Module;

@Module(
      injects = {FriendRejectActionReceiver.class},
      complete = false,
      library = true)
public class ActionReceiverModule {}
