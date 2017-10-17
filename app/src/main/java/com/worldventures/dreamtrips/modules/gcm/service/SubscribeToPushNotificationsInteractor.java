package com.worldventures.dreamtrips.modules.gcm.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.common.api.janet.command.UnsubscribeFromPushCommand;
import com.worldventures.dreamtrips.modules.common.command.SubscribeToPushNotificationsCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class SubscribeToPushNotificationsInteractor {

   private final ActionPipe<SubscribeToPushNotificationsCommand> subscribeToPushNotificationsActionPipe;
   private final ActionPipe<UnsubscribeFromPushCommand> unsubscribeFromPushCommandActionPipe;

   public SubscribeToPushNotificationsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      subscribeToPushNotificationsActionPipe = sessionActionPipeCreator.createPipe(SubscribeToPushNotificationsCommand.class, Schedulers
            .io());
      unsubscribeFromPushCommandActionPipe = sessionActionPipeCreator.createPipe(UnsubscribeFromPushCommand.class, Schedulers
            .io());
   }

   public ActionPipe<SubscribeToPushNotificationsCommand> subscribeToPushNotificationsActionPipe() {
      return subscribeToPushNotificationsActionPipe;
   }

   public ActionPipe<UnsubscribeFromPushCommand> unsubscribeFromPushCommandActionPipe() {
      return unsubscribeFromPushCommandActionPipe;
   }
}
