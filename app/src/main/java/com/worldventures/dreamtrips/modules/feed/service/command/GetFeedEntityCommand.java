package com.worldventures.dreamtrips.modules.feed.service.command;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketItemHttpAction;
import com.worldventures.dreamtrips.api.photos.GetPhotoHttpAction;
import com.worldventures.dreamtrips.api.post.GetPostHttpAction;
import com.worldventures.dreamtrips.api.trip.GetTripHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class GetFeedEntityCommand extends CommandWithError<FeedEntity> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mappery;

   private String uid;
   private FeedEntityHolder.Type type;

   public GetFeedEntityCommand(String uid, FeedEntityHolder.Type type) {
      this.uid = uid;
      this.type = type;
   }

   @Override
   protected void run(CommandCallback<FeedEntity> callback) throws Throwable {
      switch (type) {
         case TRIP:
            janet.createPipe(GetTripHttpAction.class)
                  .createObservableResult(new GetTripHttpAction(uid))
                  .map(GetTripHttpAction::response)
                  .map(tripWithDetails -> mappery.convert(tripWithDetails, TripModel.class))
                  .subscribe(callback::onSuccess, callback::onFail);
            break;
         case PHOTO:
            janet.createPipe(GetPhotoHttpAction.class)
                  .createObservableResult(new GetPhotoHttpAction(uid))
                  .map(GetPhotoHttpAction::response)
                  .map(photo -> mappery.convert(photo, Photo.class))
                  .subscribe(callback::onSuccess, callback::onFail);
            break;
         case BUCKET_LIST_ITEM:
            janet.createPipe(GetBucketItemHttpAction.class)
                  .createObservableResult(new GetBucketItemHttpAction(uid))
                  .map(GetBucketItemHttpAction::response)
                  .map(bucketItem -> mappery.convert(bucketItem, BucketItem.class))
                  .subscribe(callback::onSuccess, callback::onFail);
            break;
         case POST:
            janet.createPipe(GetPostHttpAction.class)
                  .createObservableResult(new GetPostHttpAction(uid))
                  .map(GetPostHttpAction::response)
                  .map(post -> mappery.convert(post, TextualPost.class))
                  .subscribe(callback::onSuccess, callback::onFail);
            break;
      }
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_item_details;
   }
}
