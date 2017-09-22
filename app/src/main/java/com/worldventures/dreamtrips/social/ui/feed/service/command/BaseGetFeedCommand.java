package com.worldventures.dreamtrips.social.ui.feed.service.command;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.api.feed.GetFeedHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public abstract class BaseGetFeedCommand<HttpAction extends GetFeedHttpAction> extends CommandWithError<List<FeedItem>> implements InjectableAction {

   protected static final int FEED_LIMIT = 20;
   protected static final int TIMELINE_LIMIT = 10;

   @Inject protected Janet janet;
   @Inject protected MapperyContext mappery;
   @Inject protected SessionHolder userSessionHolder;

   protected Date before;

   public BaseGetFeedCommand() {
   }

   public BaseGetFeedCommand(Date before) {
      this.before = before;
   }

   @Override
   protected void run(CommandCallback<List<FeedItem>> callback) throws Throwable {
      janet.createPipe(provideHttpActionClass())
            .createObservableResult(provideRequest())
            .map(action -> mappery.convert(action.response(), FeedItem.class))
            .subscribe(feedItems -> itemsLoaded(callback, feedItems), callback::onFail);
   }

   protected void itemsLoaded(CommandCallback<List<FeedItem>> callback, List<FeedItem> items) {
      callback.onSuccess(items);
   }

   protected abstract HttpAction provideRequest();

   protected abstract Class<HttpAction> provideHttpActionClass();
}
