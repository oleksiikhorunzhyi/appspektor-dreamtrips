package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.api.feed.ImmutableMarkFeedNotificationsReadHttpAction;
import com.worldventures.dreamtrips.api.feed.MarkFeedNotificationsReadHttpAction;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class MarkNotificationsAsReadCommand extends Command implements InjectableAction {

   @Inject Janet janet;

   private List<FeedItem> notifications;

   public MarkNotificationsAsReadCommand(List<FeedItem> notifications) {
      this.notifications = notifications;
   }

   @Override
   protected void run(CommandCallback callback) throws Throwable {
      janet.createPipe(MarkFeedNotificationsReadHttpAction.class)
            .createObservableResult(new MarkFeedNotificationsReadHttpAction(generateParams()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private ImmutableMarkFeedNotificationsReadHttpAction.Params generateParams() {
      Date since = notifications.get(notifications.size() - 1).getCreatedAt();
      Date before = notifications.get(0).getCreatedAt();
      return ImmutableMarkFeedNotificationsReadHttpAction.Params.of(since, before);
   }
}
