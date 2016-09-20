package com.worldventures.dreamtrips.modules.feed.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.feed.service.command.GetNotificationsCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.MarkNotificationAsReadCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.MarkNotificationsAsReadCommand;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.techery.janet.ActionPipe;
import rx.schedulers.Schedulers;

@Singleton
public class NotificationFeedInteractor {

   private final ActionPipe<GetNotificationsCommand> notificationsPipe;
   private final ActionPipe<MarkNotificationsAsReadCommand> markNotificationsPipe;
   private final ActionPipe<MarkNotificationAsReadCommand> markNotificationPipe;

   @Inject
   public NotificationFeedInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      notificationsPipe = sessionActionPipeCreator.createPipe(GetNotificationsCommand.class, Schedulers.io());
      markNotificationsPipe = sessionActionPipeCreator.createPipe(MarkNotificationsAsReadCommand.class, Schedulers.io());
      markNotificationPipe = sessionActionPipeCreator.createPipe(MarkNotificationAsReadCommand.class, Schedulers.io());
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
