package com.worldventures.dreamtrips.modules.feed.presenter.delegate;

import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.EditPostCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.EditPhotoWithTagsCommand;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.functions.Action2;

public class FeedEntityHolderDelegate {

   private TripImagesInteractor tripImagesInteractor;
   private PostsInteractor postsInteractor;
   private BucketInteractor bucketInteractor;

   public FeedEntityHolderDelegate(TripImagesInteractor tripImagesInteractor, PostsInteractor postsInteractor,
         BucketInteractor bucketInteractor) {
      this.tripImagesInteractor = tripImagesInteractor;
      this.postsInteractor = postsInteractor;
      this.bucketInteractor = bucketInteractor;
   }

   public void subscribeToUpdates(FeedEntityHolder feedEntityHolder, Observable.Transformer stopper,
         Action2<Command, Throwable> errorAction) {
      postsInteractor.getEditPostPipe()
            .observe()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<EditPostCommand>()
                  .onSuccess(editPostCommand -> feedEntityHolder.updateFeedEntity(editPostCommand.getResult()))
                  .onFail(errorAction::call));

      postsInteractor.deletePostPipe()
            .observeWithReplay()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<DeletePostCommand>()
                  .onSuccess(deletePostCommand -> feedEntityHolder.deleteFeedEntity(deletePostCommand.getResult()))
                  .onFail(errorAction::call));

      tripImagesInteractor.editPhotoWithTagsCommandActionPipe()
            .observeWithReplay()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<EditPhotoWithTagsCommand>()
                  .onSuccess(deletePhotoCommand -> feedEntityHolder.updateFeedEntity(deletePhotoCommand.getResult()))
                  .onFail(errorAction::call));

      tripImagesInteractor.deletePhotoPipe()
            .observeWithReplay()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<DeletePhotoCommand>()
                  .onSuccess(deletePhotoCommand -> feedEntityHolder.deleteFeedEntity(deletePhotoCommand.getResult()))
                  .onFail(errorAction::call));

      bucketInteractor.updatePipe().observe()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<UpdateBucketItemCommand>()
                  .onSuccess(updateBucketItemCommand -> feedEntityHolder.updateFeedEntity(updateBucketItemCommand.getResult()))
                  .onFail(errorAction::call));

      bucketInteractor.deleteItemPipe()
            .observeWithReplay()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<DeleteBucketItemCommand>()
                  .onSuccess(deleteItemCommand -> feedEntityHolder.deleteFeedEntity(deleteItemCommand.getResult()))
                  .onFail(errorAction::call));
   }

   protected <T> Observable.Transformer<T, T> bind(Observable.Transformer stopper) {
      return input -> input.compose(stopper);
   }
}
