package com.worldventures.dreamtrips.modules.feed.presenter.delegate;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.AddBucketItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.modules.feed.presenter.FeedEntityHolder;
import com.worldventures.dreamtrips.modules.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.CreateCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.DeleteCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.EditCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.EditPostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetCommentsCommand;
import com.worldventures.dreamtrips.modules.friends.service.FriendsInteractor;
import com.worldventures.dreamtrips.modules.friends.service.command.GetLikersCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.EditPhotoWithTagsCommand;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.functions.Action2;

public class FeedEntityHolderDelegate {

   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject PostsInteractor postsInteractor;
   @Inject BucketInteractor bucketInteractor;
   @Inject FriendsInteractor friendsInteractor;
   @Inject CommentsInteractor commentsInteractor;

   public FeedEntityHolderDelegate(Injector injector) {
      injector.inject(this);
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

      bucketInteractor.addBucketItemPhotoPipe()
            .observeWithReplay()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<AddBucketItemPhotoCommand>()
                  .onSuccess(addBucketItemPhotoCommand -> feedEntityHolder.updateFeedEntity(addBucketItemPhotoCommand.getResult().first))
                  .onFail(errorAction::call));

      bucketInteractor.deleteItemPhotoPipe()
            .observeWithReplay()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<DeleteItemPhotoCommand>()
               .onSuccess(deleteItemPhotoCommand -> feedEntityHolder.updateFeedEntity(deleteItemPhotoCommand.getResult()))
               .onFail(errorAction::call));

      friendsInteractor.getLikersPipe()
            .observeWithReplay()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<GetLikersCommand>()
               .onSuccess(getLikersCommand -> feedEntityHolder.updateFeedEntity(getLikersCommand.getFeedEntity()))
               .onFail(errorAction::call));

      commentsInteractor.commentsPipe()
            .observeWithReplay()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<GetCommentsCommand>()
                  .onSuccess(getCommentsCommand -> feedEntityHolder.updateFeedEntity(getCommentsCommand.getFeedEntity()))
                  .onFail(errorAction::call));

      commentsInteractor.createCommentPipe()
            .observeWithReplay()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<CreateCommentCommand>()
                  .onSuccess(getLikersCommand -> feedEntityHolder.updateFeedEntity(getLikersCommand.getFeedEntity()))
                  .onFail(errorAction::call));

      commentsInteractor.editCommentPipe()
            .observeWithReplay()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<EditCommentCommand>()
                  .onSuccess(getLikersCommand -> feedEntityHolder.updateFeedEntity(getLikersCommand.getFeedEntity()))
                  .onFail(errorAction::call));

      commentsInteractor.deleteCommentPipe()
            .observeWithReplay()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<DeleteCommentCommand>()
                  .onSuccess(getLikersCommand -> feedEntityHolder.updateFeedEntity(getLikersCommand.getFeedEntity()))
                  .onFail(errorAction::call));

   }

   protected <T> Observable.Transformer<T, T> bind(Observable.Transformer stopper) {
      return input -> input.compose(stopper);
   }
}
