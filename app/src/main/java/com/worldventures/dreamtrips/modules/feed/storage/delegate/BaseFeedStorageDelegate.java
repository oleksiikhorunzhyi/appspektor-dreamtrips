package com.worldventures.dreamtrips.modules.feed.storage.delegate;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperation;
import com.worldventures.dreamtrips.modules.common.list_storage.operation.ListStorageOperationFactory;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.DeletePostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.EditPostCommand;
import com.worldventures.dreamtrips.modules.feed.service.command.PostCreatedCommand;
import com.worldventures.dreamtrips.modules.feed.storage.command.FeedItemsStorageBaseCommand;
import com.worldventures.dreamtrips.modules.feed.storage.interactor.FeedItemsStorageBaseInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.DeletePhotoCommand;
import com.worldventures.dreamtrips.modules.tripsimages.service.command.EditPhotoWithTagsCommand;

import io.techery.janet.ActionState;
import rx.Observable;

public abstract class BaseFeedStorageDelegate<COMMAND extends FeedItemsStorageBaseCommand> {

   private PostsInteractor postsInteractor;
   private TripImagesInteractor tripImagesInteractor;
   private BucketInteractor bucketInteractor;
   private FeedItemsStorageBaseInteractor feedStorageInteractor;
   private SessionHolder<UserSession> sessionHolder;

   public BaseFeedStorageDelegate(FeedItemsStorageBaseInteractor<? extends COMMAND> feedStorageInteractor,
         PostsInteractor postsInteractor, TripImagesInteractor tripImagesInteractor, BucketInteractor bucketInteractor,
         SessionHolder<UserSession> sessionHolder) {
      this.feedStorageInteractor = feedStorageInteractor;
      this.postsInteractor = postsInteractor;
      this.tripImagesInteractor = tripImagesInteractor;
      this.bucketInteractor = bucketInteractor;
      this.sessionHolder = sessionHolder;
   }

   protected Observable<ListStorageOperation> getListStorageOperationObservable() {
      return Observable.merge(postsInteractor.postCreatedPipe().observeSuccess()
                  .map(PostCreatedCommand::getResult)
                  .map(this::createFeedItem)
                  .map(ListStorageOperationFactory::addItemToBeginningOperation),

            postsInteractor.getEditPostPipe().observeSuccess()
                  .map(EditPostCommand::getResult)
                  .map(this::createFeedItem)
                  .map(ListStorageOperationFactory::updateItemOperation),

            postsInteractor.deletePostPipe().observeSuccess()
                  .map(DeletePostCommand::getResult)
                  .map(this::createFeedItem)
                  .map(ListStorageOperationFactory::deleteItemOperation),

            tripImagesInteractor.editPhotoWithTagsCommandActionPipe().observeSuccess()
                  .map(EditPhotoWithTagsCommand::getResult)
                  .map(this::createFeedItem)
                  .map(ListStorageOperationFactory::addItemToBeginningOperation),

            tripImagesInteractor.deletePhotoPipe().observeSuccess()
                  .map(DeletePhotoCommand::getResult)
                  .map(this::createFeedItem)
                  .map(ListStorageOperationFactory::deleteItemOperation),

            bucketInteractor.updatePipe().observeSuccess()
                  .map(UpdateBucketItemCommand::getResult)
                  .map(this::createFeedItem)
                  .map(ListStorageOperationFactory::updateItemOperation),

            bucketInteractor.deleteItemPipe().observeSuccess()
                  .map(DeleteBucketItemCommand::getResult)
                  .map(this::createFeedItem)
                  .map(ListStorageOperationFactory::deleteItemOperation));
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
