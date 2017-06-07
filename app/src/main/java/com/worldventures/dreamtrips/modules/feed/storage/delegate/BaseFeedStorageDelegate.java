package com.worldventures.dreamtrips.modules.feed.storage.delegate;

import com.techery.spares.module.Injector;
import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperationFactory;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.service.CommentsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.FeedInteractor;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.ChangeFeedEntityLikedStatusCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.CreateCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.DeleteCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.EditCommentCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.EditPostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetCommentsCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.GetFeedEntityCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.PostCreatedCommand;
import com.worldventures.dreamtrips.modules.feed.storage.command.FeedItemsStorageBaseCommand;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.FeedItemsStorageBaseInteractor;
import com.worldventures.dreamtrips.modules.friends.service.FriendsInteractor;
import com.worldventures.dreamtrips.modules.friends.service.command.GetLikersCommand;
import com.worldventures.dreamtrips.modules.trips.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.EditPhotoWithTagsCommand;

import java.util.Arrays;

import javax.inject.Inject;

import io.techery.janet.ActionState;
import rx.Observable;

public abstract class BaseFeedStorageDelegate<COMMAND extends FeedItemsStorageBaseCommand> {

   protected FeedItemsStorageBaseInteractor feedStorageInteractor;

   @Inject FeedInteractor feedInteractor;
   @Inject PostsInteractor postsInteractor;
   @Inject TripsInteractor tripsInteractor;
   @Inject TripImagesInteractor tripImagesInteractor;
   @Inject BucketInteractor bucketInteractor;
   @Inject FriendsInteractor friendsInteractor;
   @Inject CommentsInteractor commentsInteractor;

   @Inject SessionHolder<UserSession> sessionHolder;

   public BaseFeedStorageDelegate(FeedItemsStorageBaseInteractor<? extends COMMAND> feedStorageInteractor, Injector injector) {
      this.feedStorageInteractor = feedStorageInteractor;
      injector.inject(this);
   }

   protected Observable<ListStorageOperation> getListStorageOperationObservable() {
      return Observable.merge(Arrays.asList(
            postsInteractor.postCreatedPipe().observeSuccess()
                  .map(PostCreatedCommand::getFeedItem)
                  .map(ListStorageOperationFactory::addItemToBeginningOperation),

            tripImagesInteractor.deletePhotoPipe().observeSuccess()
                  .map(DeletePhotoCommand::getResult)
                  .map(this::createFeedItem)
                  .map(ListStorageOperationFactory::deleteItemOperation),

            postsInteractor.deletePostPipe().observeSuccess()
                  .map(DeletePostCommand::getResult)
                  .map(this::createFeedItem)
                  .map(ListStorageOperationFactory::deleteItemOperation),

            postsInteractor.getEditPostPipe().observeSuccess()
                  .map(EditPostCommand::getResult)
                  .map(this::updateItemOperation),

            tripImagesInteractor.editPhotoWithTagsCommandActionPipe().observeSuccess()
                  .map(EditPhotoWithTagsCommand::getResult)
                  .map(this::updateItemOperation),

            bucketInteractor.updatePipe().observeSuccess()
                  .map(UpdateBucketItemCommand::getResult)
                  .map(this::updateItemOperation),

            bucketInteractor.addBucketItemPhotoPipe()
                  .observeSuccess()
                  .map(addBucketItemPhotoCommand -> addBucketItemPhotoCommand.getResult().first)
                  .map(this::updateItemOperation),

            bucketInteractor.deleteItemPhotoPipe().observeSuccess()
                  .map(DeleteItemPhotoCommand::getResult)
                  .map(this::updateItemOperation),

            bucketInteractor.deleteItemPipe().observeSuccess()
                  .map(DeleteBucketItemCommand::getResult)
                  .map(this::createFeedItem)
                  .map(ListStorageOperationFactory::deleteItemOperation),

            friendsInteractor.getLikersPipe().observeSuccess()
                  .map(GetLikersCommand::getFeedEntity)
                  .map(this::updateItemOperation),

            feedInteractor.changeFeedEntityLikedStatusPipe().observeSuccess()
                  .map(ChangeFeedEntityLikedStatusCommand::getResult)
                  .map(this::updateItemOperation),

            feedInteractor.getFeedEntityPipe().observeSuccess()
                  .map(GetFeedEntityCommand::getResult)
                  .map(this::updateItemOperation),

            tripsInteractor.detailsPipe().observeSuccess()
                  .map(GetTripDetailsCommand::getResult)
                  .map(this::updateItemOperation),

            commentsInteractor.commentsPipe().observeSuccess()
                  .map(GetCommentsCommand::getFeedEntity)
                  .map(this::updateItemOperation),

            commentsInteractor.createCommentPipe().observeSuccess()
                  .map(CreateCommentCommand::getFeedEntity)
                  .map(this::updateItemOperation),

            commentsInteractor.editCommentPipe().observeSuccess()
                  .map(EditCommentCommand::getFeedEntity)
                  .map(this::updateItemOperation),

            commentsInteractor.deleteCommentPipe().observeSuccess()
                  .map(DeleteCommentCommand::getFeedEntity)
                  .map(this::updateItemOperation)
      ));
   }

   protected ListStorageOperation updateItemOperation(FeedEntity feedEntity) {
      return new UpdateFeedEntityStorageOperation(feedEntity);
   }

   private FeedItem createFeedItem(FeedEntity feedEntity) {
      return FeedItem.create(feedEntity, sessionHolder.get().get().getUser());
   }

   public Observable<ActionState<COMMAND>> startUpdatingStorage() {
      return getListStorageOperationObservable()
            .flatMap(listStorageOperation
                  -> feedStorageInteractor.getFeedItemsStoragePipe().createObservable(createCommand(listStorageOperation)));
   }

   protected abstract COMMAND createCommand(ListStorageOperation listStorageOperation);
}
