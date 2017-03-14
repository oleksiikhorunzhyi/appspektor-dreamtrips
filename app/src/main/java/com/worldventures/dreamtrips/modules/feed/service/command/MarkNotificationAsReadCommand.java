package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.api.feed.MarkFeedNotificationReadHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;

import javax.inject.Inject;
import javax.inject.Named;

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
