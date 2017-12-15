package com.worldventures.dreamtrips.social.ui.feed.storage.delegate;

import com.worldventures.dreamtrips.modules.common.list_storage.operation.AddToBeginningIfNotExistsStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.modules.trips.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.social.ui.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.UidItem;
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
import com.worldventures.dreamtrips.social.ui.feed.service.command.GetFeedEntityCommand;
import com.worldventures.dreamtrips.social.ui.feed.service.command.PostCreatedCommand;
import com.worldventures.dreamtrips.social.ui.feed.storage.command.FeedItemsStorageBaseCommand;
import com.worldventures.dreamtrips.social.ui.feed.storage.interactor.FeedItemsStorageBaseInteractor;
import com.worldventures.dreamtrips.social.service.friends.interactor.FriendsInteractor;
import com.worldventures.dreamtrips.social.service.friends.interactor.command.GetLikersCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeletePhotoCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.DeleteVideoCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.EditPhotoWithTagsCommand;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.techery.janet.ActionState;
import rx.Observable;

public abstract class BaseFeedStorageDelegate<COMMAND extends FeedItemsStorageBaseCommand> {

   private final FeedItemsStorageBaseInteractor feedStorageInteractor;
   protected final FeedInteractor feedInteractor;
   private final PostsInteractor postsInteractor;
   private final TripsInteractor tripsInteractor;
   private final TripImagesInteractor tripImagesInteractor;
   private final BucketInteractor bucketInteractor;
   private final FriendsInteractor friendsInteractor;
   private final CommentsInteractor commentsInteractor;

   public BaseFeedStorageDelegate(FeedItemsStorageBaseInteractor<? extends COMMAND> feedStorageInteractor, FeedInteractor feedInteractor,
         PostsInteractor postsInteractor, TripsInteractor tripsInteractor, TripImagesInteractor tripImagesInteractor,
         BucketInteractor bucketInteractor, FriendsInteractor friendsInteractor, CommentsInteractor commentsInteractor) {
      this.feedStorageInteractor = feedStorageInteractor;

      this.feedInteractor = feedInteractor;
      this.postsInteractor = postsInteractor;
      this.tripsInteractor = tripsInteractor;
      this.tripImagesInteractor = tripImagesInteractor;
      this.bucketInteractor = bucketInteractor;
      this.friendsInteractor = friendsInteractor;
      this.commentsInteractor = commentsInteractor;
   }

   protected Observable<ListStorageOperation> getListStorageOperationObservable() {
      return Observable.merge(Arrays.asList(
            postsInteractor.postCreatedPipe().observeSuccess()
                  .map(PostCreatedCommand::getFeedItem)
                  .map(AddToBeginningIfNotExistsStorageOperation::new),

            tripImagesInteractor.deletePhotoPipe().observeSuccess()
                  .map(DeletePhotoCommand::getResult)
                  .map(UidItem::getUid)
                  .map(this::deleteItemOperation),

            postsInteractor.deletePostPipe().observeSuccess()
                  .map(DeletePostCommand::getResult)
                  .map(UidItem::getUid)
                  .map(this::deleteItemOperation),

            feedInteractor.deleteVideoPipe().observeSuccess()
                  .map(DeleteVideoCommand::getResult)
                  .map(UidItem::getUid)
                  .map(this::deleteItemOperation),

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
                  .map(UidItem::getUid)
                  .map(this::deleteItemOperation),

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

   protected ListStorageOperation deleteItemOperation(String uid) {
      return new DeleteFeedItemStorageOperation(uid);
   }

   public Observable<ActionState<COMMAND>> startUpdatingStorage() {
      return getListStorageOperationObservable()
            .flatMap(listStorageOperation
                  -> feedStorageInteractor.getFeedItemsStoragePipe()
                  .createObservable(createCommand(listStorageOperation)));
   }

   public Observable<COMMAND> observeStorageCommand() {
      return getListStorageOperationObservable()
            .flatMap(listStorageOperation
                  -> feedStorageInteractor.getFeedItemsStoragePipe()
                  .createObservableResult(createCommand(listStorageOperation)));
   }

   protected abstract COMMAND createCommand(ListStorageOperation listStorageOperation);

   public class DeleteFeedItemStorageOperation implements ListStorageOperation<FeedItem<FeedEntity>> {

      private final String uid;

      public DeleteFeedItemStorageOperation(String uid) {
         this.uid = uid;
      }

      @Override
      public List<FeedItem<FeedEntity>> perform(List<FeedItem<FeedEntity>> items) {
         Iterator<FeedItem<FeedEntity>> itemIterator = items.iterator();
         while (itemIterator.hasNext()) {
            FeedEntity entity = itemIterator.next().getItem();
            if (entity.getUid().equals(uid)) {
               itemIterator.remove();
            } else if (entity instanceof TextualPost) {
               TextualPost post = (TextualPost) entity;
               Iterator<FeedEntityHolder> attachmentIterator = post.getAttachments().iterator();
               while (attachmentIterator.hasNext()) {
                  FeedEntity attachment = attachmentIterator.next().getItem();
                  if (attachment.getUid().equals(uid)) {
                     if (post.getAttachments().size() == 1) {
                        itemIterator.remove();
                     } else {
                        attachmentIterator.remove();
                     }
                  }
               }
            }
         }
         return items;
      }
   }
}
