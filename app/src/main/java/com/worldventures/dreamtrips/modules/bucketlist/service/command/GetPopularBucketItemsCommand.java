package com.worldventures.dreamtrips.modules.bucketlist.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketListActivitiesHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketListDiningsHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketListLocationsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

@CommandAction
public class GetPopularBucketItemsCommand extends CommandWithError<List<PopularBucketItem>> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;

   private BucketItem.BucketType type;

   public GetPopularBucketItemsCommand(BucketItem.BucketType type) {
      this.type = type;
   }

   @Override
   protected void run(CommandCallback<List<PopularBucketItem>> callback) throws Throwable {
      loadPopularItems().subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<List<PopularBucketItem>> loadPopularItems() {
      switch (type) {
         case LOCATION:
            return janet.createPipe(GetBucketListLocationsHttpAction.class)
                  .createObservableResult(new GetBucketListLocationsHttpAction())
                  .map(GetBucketListLocationsHttpAction::response)
                  .map(items -> mapperyContext.convert(items, PopularBucketItem.class));
         case ACTIVITY:
            return janet.createPipe(GetBucketListActivitiesHttpAction.class)
                  .createObservableResult(new GetBucketListActivitiesHttpAction())
                  .map(GetBucketListActivitiesHttpAction::response)
                  .map(items -> mapperyContext.convert(items, PopularBucketItem.class));
         case DINING:
            return janet.createPipe(GetBucketListDiningsHttpAction.class)
                  .createObservableResult(new GetBucketListDiningsHttpAction())
                  .map(GetBucketListDiningsHttpAction::response)
                  .map(items -> mapperyContext.convert(items, PopularBucketItem.class));
      }
      return Observable.error(new IllegalStateException("Wrong type was passed"));
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_popular_bl;
   }
}
