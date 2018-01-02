package com.worldventures.dreamtrips.social.ui.feed.presenter.delegate;

import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.AddBucketItemPhotoCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.social.ui.feed.presenter.FeedEntityHolder;
import com.worldventures.dreamtrips.social.ui.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.CreateCommentCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.DeleteCommentCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.DeletePostCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.EditCommentCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.EditPostCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetCommentsCommand;
import com.worldventures.dreamtrips.social.service.friends.interactor.FriendsInteractor;
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetLikersCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeletePhotoCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeleteVideoCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.EditPhotoWithTagsCommand;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.functions.Action2;

public class FeedEntityHolderDelegate {

   private final TripImagesInteractor tripImagesInteractor;
   private final FeedInteractor feedInteractor;
   private final PostsInteractor postsInteractor;
   private final BucketInteractor bucketInteractor;
   private final FriendsInteractor friendsInteractor;
   private final CommentsInteractor commentsInteractor;

   public FeedEntityHolderDelegate(TripImagesInteractor tripImagesInteractor, FeedInteractor feedInteractor,
         PostsInteractor postsInteractor, BucketInteractor bucketInteractor, FriendsInteractor friendsInteractor,
         CommentsInteractor commentsInteractor) {

      this.tripImagesInteractor = tripImagesInteractor;
      this.feedInteractor = feedInteractor;
      this.postsInteractor = postsInteractor;
      this.bucketInteractor = bucketInteractor;
      this.friendsInteractor = friendsInteractor;
      this.commentsInteractor = commentsInteractor;
   }

   public void subscribeToUpdates(FeedEntityHolder feedEntityHolder, Observable.Transformer stopper,
         Action2<Command, Throwable> errorAction) {
      postsInteractor.getEditPostPipe()
            .observe()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<EditPostCommand>()
                  .onSuccess(editPostCommand -> feedEntityHolder.updateFeedEntity(editPostCommand.getResult()))
                  .onFail(errorAction::call));

      feedInteractor.changeFeedEntityLikedStatusPipe()
            .observe()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<ChangeFeedEntityLikedStatusCommand>()
                  .onSuccess(changeFeedEntityLikedStatusCommand -> feedEntityHolder.updateFeedEntity(changeFeedEntityLikedStatusCommand
                        .getResult()))
                  .onFail(errorAction::call));

      postsInteractor.deletePostPipe()
            .observeWithReplay()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<DeletePostCommand>()
                  .onSuccess(deletePostCommand -> feedEntityHolder.deleteFeedEntity(deletePostCommand.getResult()))
                  .onFail(errorAction::call));

      feedInteractor.deleteVideoPipe()
            .observe()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<DeleteVideoCommand>()
                  .onSuccess(deletePostCommand -> feedEntityHolder.deleteFeedEntity(deletePostCommand.getResult()))
                  .onFail(errorAction::call));

      tripImagesInteractor.getEditPhotoWithTagsCommandActionPipe()
            .observeWithReplay()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<EditPhotoWithTagsCommand>()
                  .onSuccess(deletePhotoCommand -> feedEntityHolder.updateFeedEntity(deletePhotoCommand.getResult()))
                  .onFail(errorAction::call));

      tripImagesInteractor.getDeletePhotoPipe()
            .observe()
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
            .observe()
            .compose(bind(stopper))
            .subscribe(new ActionStateSubscriber<CreateCommentCommand>()
                  .onSuccess(command -> feedEntityHolder.updateFeedEntity(command.getFeedEntity()))
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
