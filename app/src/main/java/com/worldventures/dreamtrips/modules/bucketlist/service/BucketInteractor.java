package com.worldventures.dreamtrips.modules.bucketlist.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.CreateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.AddBucketItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.ChangeBucketListOrderCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteBucketItemCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.FindBucketItemByPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.GetCategoriesCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.GetPopularBucketItemSuggestionsCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.GetPopularBucketItemsCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.MergeBucketItemPhotosWithStorageCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.RecentlyAddedBucketsFromPopularCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.UploadPhotoControllerCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.CancelException;
import io.techery.janet.WriteActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.schedulers.Schedulers;

public final class BucketInteractor {
   private final ActionPipe<CreateBucketItemCommand> createBucketPipe;
   private final ActionPipe<UpdateBucketItemCommand> updateItemPipe;
   private final ActionPipe<ChangeBucketListOrderCommand> moveItemPipe;
   private final ActionPipe<DeleteBucketItemCommand> deleteItemPipe;
   private final ActionPipe<DeleteItemPhotoCommand> deleteItemPhotoPipe;
   private final ActionPipe<AddBucketItemPhotoCommand> addBucketItemPhotoPipe;
   private final ActionPipe<GetCategoriesCommand> getCategoriesPipe;
   private final ActionPipe<GetPopularBucketItemSuggestionsCommand> getPopularBucketItemSuggestionsPipe;
   private final ActionPipe<GetPopularBucketItemsCommand> getPopularBucketItemsPipe;

   private final ActionPipe<BucketListCommand> bucketListActionPipe;
   private final ActionPipe<FindBucketItemByPhotoCommand> findBucketItemByPhotoActionPipe;
   private final ActionPipe<UploadPhotoControllerCommand> uploadPhotoControllerCommandPipe;
   private final WriteActionPipe<MergeBucketItemPhotosWithStorageCommand> mergeBucketItemPhotosWithStorageCommandPipe;
   private final ActionPipe<RecentlyAddedBucketsFromPopularCommand> recentlyAddedBucketsFromPopularCommandPipe;

   public BucketInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      createBucketPipe = sessionActionPipeCreator.createPipe(CreateBucketItemCommand.class, Schedulers.io());
      updateItemPipe = sessionActionPipeCreator.createPipe(UpdateBucketItemCommand.class, Schedulers.io());
      moveItemPipe = sessionActionPipeCreator.createPipe(ChangeBucketListOrderCommand.class, Schedulers.io());
      deleteItemPipe = sessionActionPipeCreator.createPipe(DeleteBucketItemCommand.class, Schedulers.io());
      deleteItemPhotoPipe = sessionActionPipeCreator.createPipe(DeleteItemPhotoCommand.class, Schedulers.io());
      addBucketItemPhotoPipe = sessionActionPipeCreator.createPipe(AddBucketItemPhotoCommand.class, Schedulers.io());
      getCategoriesPipe = sessionActionPipeCreator.createPipe(GetCategoriesCommand.class, Schedulers.io());
      getPopularBucketItemSuggestionsPipe = sessionActionPipeCreator.createPipe(GetPopularBucketItemSuggestionsCommand.class, Schedulers.io());
      getPopularBucketItemsPipe = sessionActionPipeCreator.createPipe(GetPopularBucketItemsCommand.class, Schedulers.io());
      bucketListActionPipe = sessionActionPipeCreator.createPipe(BucketListCommand.class, Schedulers.io());
      findBucketItemByPhotoActionPipe = sessionActionPipeCreator.createPipe(FindBucketItemByPhotoCommand.class, Schedulers.immediate());
      uploadPhotoControllerCommandPipe = sessionActionPipeCreator.createPipe(UploadPhotoControllerCommand.class, Schedulers.immediate());
      mergeBucketItemPhotosWithStorageCommandPipe = sessionActionPipeCreator.createPipe(MergeBucketItemPhotosWithStorageCommand.class, Schedulers
            .immediate());
      recentlyAddedBucketsFromPopularCommandPipe = sessionActionPipeCreator.createPipe(RecentlyAddedBucketsFromPopularCommand.class, Schedulers
            .immediate());

      connect();
   }

   public ActionPipe<CreateBucketItemCommand> createPipe() {
      return createBucketPipe;
   }

   public ActionPipe<UpdateBucketItemCommand> updatePipe() {
      return updateItemPipe;
   }

   public ActionPipe<ChangeBucketListOrderCommand> movePipe() {
      return moveItemPipe;
   }

   public ActionPipe<DeleteBucketItemCommand> deleteItemPipe() {
      return deleteItemPipe;
   }

   public ActionPipe<DeleteItemPhotoCommand> deleteItemPhotoPipe() {
      return deleteItemPhotoPipe;
   }

   public ActionPipe<AddBucketItemPhotoCommand> addBucketItemPhotoPipe() {
      return addBucketItemPhotoPipe;
   }

   public ActionPipe<GetCategoriesCommand> getCategoriesPipe() {
      return getCategoriesPipe;
   }

   public ActionPipe<GetPopularBucketItemSuggestionsCommand> getPopularBucketItemSuggestionsPipe() {
      return getPopularBucketItemSuggestionsPipe;
   }

   public ActionPipe<GetPopularBucketItemsCommand> getPopularBucketItemsPipe() {
      return getPopularBucketItemsPipe;
   }

   public ActionPipe<BucketListCommand> bucketListActionPipe() {
      return bucketListActionPipe;
   }

   public ActionPipe<FindBucketItemByPhotoCommand> findBucketItemByPhotoActionPipe() {
      return findBucketItemByPhotoActionPipe;
   }

   public ActionPipe<UploadPhotoControllerCommand> uploadControllerCommandPipe() {
      return uploadPhotoControllerCommandPipe;
   }

   public WriteActionPipe<MergeBucketItemPhotosWithStorageCommand> mergeBucketItemPhotosWithStorageCommandPipe() {
      return mergeBucketItemPhotosWithStorageCommandPipe;
   }

   public ActionPipe<RecentlyAddedBucketsFromPopularCommand> recentlyAddedBucketsFromPopularCommandPipe() {
      return recentlyAddedBucketsFromPopularCommandPipe;
   }

   private void connect() {
      createBucketPipe.observeSuccess().map(CreateBucketItemCommand::getResult).subscribe(item -> {
         bucketListActionPipe.send(BucketListCommand.createItem(item));
      });
      Observable.merge(updateItemPipe.observeSuccess()
            .map(UpdateBucketItemCommand::getResult), deleteItemPhotoPipe.observeSuccess()
            .map(DeleteItemPhotoCommand::getResult), addBucketItemPhotoPipe.observeSuccess()
            .map(AddBucketItemPhotoCommand::getResult)
            .map(bucketItemBucketPhotoPair -> bucketItemBucketPhotoPair.first)).subscribe(bucketItem -> {
         bucketListActionPipe.send(BucketListCommand.updateItem(bucketItem));
      });
      deleteItemPipe.observeSuccess().map(command -> command.getResult().getUid()).subscribe(bucketItemUid -> {
         bucketListActionPipe.send(BucketListCommand.deleteItem(bucketItemUid));
      });
      addBucketItemPhotoPipe.observe()
            .subscribe(new ActionStateSubscriber<AddBucketItemPhotoCommand>().onStart(this::sendCreateActionUploadCommandInternal)
                  .onSuccess(this::sendCreateActionUploadCommandInternal)
                  .onFail((command, throwable) -> {
                     if (throwable instanceof CancelException) {
                        uploadPhotoControllerCommandPipe.
                              send(UploadPhotoControllerCommand.cancel(command.bucketUid(), command.photoEntityStateHolder()));
                        return;
                     }

                     sendCreateActionUploadCommandInternal(command);
                  }));
   }

   private void sendCreateActionUploadCommandInternal(AddBucketItemPhotoCommand command) {
      uploadControllerCommandPipe().send(UploadPhotoControllerCommand.create(command.bucketUid(), command.photoEntityStateHolder()));
   }
}
