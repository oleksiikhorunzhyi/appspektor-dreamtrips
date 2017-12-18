package com.worldventures.dreamtrips.modules.common.service;

import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.common.command.NotificationCountChangedCommand;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

public class UserNotificationInteractor {

   private final ActionPipe<NotificationCountChangedCommand> notificationCountChangedPipe;

   public UserNotificationInteractor(SessionActionPipeCreator pipeCreator) {
      this.notificationCountChangedPipe = pipeCreator.createPipe(NotificationCountChangedCommand.class, Schedulers.io());
   }

   public ActionPipe<NotificationCountChangedCommand> notificationCountChangedPipe() {
      return notificationCountChangedPipe;
   }
}
