package com.worldventures.dreamtrips.modules.common.service;

import com.worldventures.dreamtrips.modules.common.api.janet.command.SubscribeToPushNotificationsCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class SubscribeToPushNotificationsInteractor {

   private ActionPipe<SubscribeToPushNotificationsCommand> subscribeToPushNotificationsActionPipe;

   @Inject
   public SubscribeToPushNotificationsInteractor(Janet janet) {
      subscribeToPushNotificationsActionPipe = janet.createPipe(SubscribeToPushNotificationsCommand.class, Schedulers.io());
   }

   public ActionPipe<SubscribeToPushNotificationsCommand> subscribeToPushNotificationsActionPipe() {
      return subscribeToPushNotificationsActionPipe;
   }
}
