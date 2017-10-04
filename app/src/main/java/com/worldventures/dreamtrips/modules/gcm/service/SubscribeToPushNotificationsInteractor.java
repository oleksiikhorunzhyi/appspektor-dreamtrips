package com.worldventures.dreamtrips.modules.gcm.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.common.command.SubscribeToPushNotificationsCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class SubscribeToPushNotificationsInteractor {

   private ActionPipe<SubscribeToPushNotificationsCommand> subscribeToPushNotificationsActionPipe;

   @Inject
   public SubscribeToPushNotificationsInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      subscribeToPushNotificationsActionPipe = sessionActionPipeCreator.createPipe(SubscribeToPushNotificationsCommand.class, Schedulers.io());
   }

   public ActionPipe<SubscribeToPushNotificationsCommand> subscribeToPushNotificationsActionPipe() {
      return subscribeToPushNotificationsActionPipe;
   }
}
