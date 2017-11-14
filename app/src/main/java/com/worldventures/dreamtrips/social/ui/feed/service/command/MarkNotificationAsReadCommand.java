package com.worldventures.dreamtrips.social.ui.feed.service.command;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.dreamtrips.api.feed.MarkFeedNotificationReadHttpAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class MarkNotificationAsReadCommand extends Command implements InjectableAction {

   @Inject Janet janet;

   private int notificationId;

   public MarkNotificationAsReadCommand(int notificationId) {
      this.notificationId = notificationId;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(MarkFeedNotificationReadHttpAction.class)
            .createObservableResult(new MarkFeedNotificationReadHttpAction(notificationId))
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
