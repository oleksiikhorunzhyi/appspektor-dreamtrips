package com.worldventures.dreamtrips.modules.bucketlist.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketListPopularActivitiesHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketListPopularDinningsHttpAction;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketListPopularLocationsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.model.PopularBucketItem;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

@CommandAction
public class GetPopularBucketItemSuggestionsCommand extends CommandWithError<List<PopularBucketItem>> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;
   @Inject MapperyContext mapperyContext;

   private BucketItem.BucketType type;
   private String query;

   public GetPopularBucketItemSuggestionsCommand(BucketItem.BucketType type, String query) {
      this.type = type;
      this.query = query;
   }

   @Override
   protected void run(CommandCallback<List<PopularBucketItem>> callback) throws Throwable {
      loadPopularItems().subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<List<PopularBucketItem>> loadPopularItems() {
      switch (type) {
         case LOCATION:
            return janet.createPipe(GetBucketListPopularLocationsHttpAction.class)
                  .createObservableResult(new GetBucketListPopularLocationsHttpAction(query))
                  .map(GetBucketListPopularLocationsHttpAction::response)
                  .map(items -> mapperyContext.convert(items, PopularBucketItem.class));
         case ACTIVITY:
            return janet.createPipe(GetBucketListPopularActivitiesHttpAction.class)
                  .createObservableResult(new GetBucketListPopularActivitiesHttpAction(query))
                  .map(GetBucketListPopularActivitiesHttpAction::response)
                  .map(items -> mapperyContext.convert(items, PopularBucketItem.class));
         case DINING:
            return janet.createPipe(GetBucketListPopularDinningsHttpAction.class)
                  .createObservableResult(new GetBucketListPopularDinningsHttpAction(query))
                  .map(GetBucketListPopularDinningsHttpAction::response)
                  .map(items -> mapperyContext.convert(items, PopularBucketItem.class));
      }
      return Observable.error(new IllegalStateException("Wrong type was passed"));
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_suggestions;
   }
}
