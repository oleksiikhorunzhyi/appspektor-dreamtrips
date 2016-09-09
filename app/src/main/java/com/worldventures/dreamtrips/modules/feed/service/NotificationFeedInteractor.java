package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.modules.feed.service.command.GetNotificationsCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.MarkNotificationAsReadCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.MarkNotificationsAsReadCommand;

import javax.inject.Inject;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import rx.schedulers.Schedulers;

public class NotificationFeedInteractor {

   private final ActionPipe<GetNotificationsCommand> notificationsPipe;
   private final ActionPipe<MarkNotificationsAsReadCommand> markNotificationsPipe;
   private final ActionPipe<MarkNotificationAsReadCommand> markNotificationPipe;

   @Inject
   public NotificationFeedInteractor(Janet janet) {
      notificationsPipe = janet.createPipe(GetNotificationsCommand.class, Schedulers.io());
      markNotificationsPipe = janet.createPipe(MarkNotificationsAsReadCommand.class, Schedulers.io());
      markNotificationPipe = janet.createPipe(MarkNotificationAsReadCommand.class, Schedulers.io());
   }

   public ActionPipe<GetNotificationsCommand> notificationsPipe() {
      return notificationsPipe;
   }

   public ActionPipe<MarkNotificationsAsReadCommand> markNotificationsPipe() {
      return markNotificationsPipe;
   }

   public ActionPipe<MarkNotificationAsReadCommand> markNotificationPipe() {
      return markNotificationPipe;
   }
}
