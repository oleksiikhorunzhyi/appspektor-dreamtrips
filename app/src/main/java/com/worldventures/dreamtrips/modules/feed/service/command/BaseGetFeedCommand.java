package com.worldventures.dreamtrips.modules.feed.service.command;

import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.rx.composer.ListFilter;
import com.worldventures.dreamtrips.core.rx.composer.ListMapper;
import com.worldventures.dreamtrips.core.utils.DateTimeUtils;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.model.feed.base.ParentFeedItem;
import com.worldventures.dreamtrips.modules.feed.service.api.GetFeedHttpAction;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Janet;
import rx.Observable;
import rx.schedulers.Schedulers;

public abstract class BaseGetFeedCommand<T extends GetFeedHttpAction> extends CommandWithError<List<FeedItem<FeedEntity>>> implements InjectableAction {

   protected static final int FEED_LIMIT = 20;
   protected static final int TIMELINE_LIMIT = 10;

   @Inject Janet janet;

   protected String before;

   public BaseGetFeedCommand() {
   }

   public BaseGetFeedCommand(Date before) {
      this.before = getBeforeDateString(before);
   }

   protected String getBeforeDateString(Date beforeDate) {
      return beforeDate == null ? null : DateTimeUtils.convertDateToUTCString(beforeDate);
   }

   @Override
   protected void run(CommandCallback<List<FeedItem<FeedEntity>>> callback) throws Throwable {
      janet.createPipe(provideHttpActionClass(), Schedulers.io())
            .createObservableResult(provideRequest())
            .map(GetFeedHttpAction::getResponseItems)
            .compose(new ListFilter<>(ParentFeedItem::isSingle))
            .compose(new ListMapper<>(parentFeedItem -> parentFeedItem.getItems().get(0)))
            .subscribe(items -> itemsLoaded(callback, items), callback::onFail);
   }

   protected void itemsLoaded(CommandCallback<List<FeedItem<FeedEntity>>> callback, List<FeedItem<FeedEntity>> items) {
      callback.onSuccess(items);
   }

   protected abstract Class<T> provideHttpActionClass();

   protected abstract T provideRequest();
}
