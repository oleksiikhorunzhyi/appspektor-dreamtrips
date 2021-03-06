package com.worldventures.dreamtrips.social.ui.feed.service.command;


import com.worldventures.core.janet.CommandWithError;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.bucketlist.GetBucketItemHttpAction;
import com.worldventures.dreamtrips.api.multimedia.GetVideoHttpAction;
import com.worldventures.dreamtrips.api.photos.GetPhotoHttpAction;
import com.worldventures.dreamtrips.api.post.GetPostHttpAction;
import com.worldventures.dreamtrips.api.trip.GetTripHttpAction;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

import javax.inject.Inject;

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
         case VIDEO:
            janet.createPipe(GetVideoHttpAction.class)
                  .createObservableResult(new GetVideoHttpAction(uid))
                  .map(GetVideoHttpAction::response)
                  .map(video -> mappery.convert(video, Video.class))
                  .subscribe(callback::onSuccess, callback::onFail);
            break;
         default:
            break;
      }
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_load_item_details;
   }
}
