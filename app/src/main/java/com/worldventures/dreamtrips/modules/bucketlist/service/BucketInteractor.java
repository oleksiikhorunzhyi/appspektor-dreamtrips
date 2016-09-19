package com.worldventures.dreamtrips.modules.bucketlist.service;

import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.ChangeOrderHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.CreateBucketItemHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.DeleteItemHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.LoadBucketListFullHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.MarkItemAsDoneHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.action.UpdateItemHttpAction;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.AddBucketItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.BucketListCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.DeleteItemPhotoCommand;
import com.worldventures.dreamtrips.modules.bucketlist.service.command.FindBucketItemByPhotoCommand;
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
   private final ActionPipe<LoadBucketListFullHttpAction> loadBucketListPipe;
   private final ActionPipe<CreateBucketItemHttpAction> createBucketPipe;
   private final ActionPipe<UpdateItemHttpAction> updateItemPipe;
   private final ActionPipe<ChangeOrderHttpAction> moveItemPipe;
   private final ActionPipe<MarkItemAsDoneHttpAction> markItemAsDonePipe;
   private final ActionPipe<DeleteItemHttpAction> deleteItemPipe;
   private final ActionPipe<DeleteItemPhotoCommand> deleteItemPhotoPipe;
   private final ActionPipe<AddBucketItemPhotoCommand> addBucketItemPhotoPipe;

   private final ActionPipe<BucketListCommand> bucketListActionPipe;
   private final ActionPipe<FindBucketItemByPhotoCommand> findBucketItemByPhotoActionPipe;
   private final ActionPipe<UploadPhotoControllerCommand> uploadPhotoControllerCommandPipe;
   private final WriteActionPipe<MergeBucketItemPhotosWithStorageCommand> mergeBucketItemPhotosWithStorageCommandPipe;
   private final ActionPipe<RecentlyAddedBucketsFromPopularCommand> recentlyAddedBucketsFromPopularCommandPipe;

   public BucketInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      loadBucketListPipe = sessionActionPipeCreator.createPipe(LoadBucketListFullHttpAction.class, Schedulers.io());
      createBucketPipe = sessionActionPipeCreator.createPipe(CreateBucketItemHttpAction.class, Schedulers.io());
      updateItemPipe = sessionActionPipeCreator.createPipe(UpdateItemHttpAction.class, Schedulers.io());
      moveItemPipe = sessionActionPipeCreator.createPipe(ChangeOrderHttpAction.class, Schedulers.io());
      markItemAsDonePipe = sessionActionPipeCreator.createPipe(MarkItemAsDoneHttpAction.class, Schedulers.io());
      deleteItemPipe = sessionActionPipeCreator.createPipe(DeleteItemHttpAction.class, Schedulers.io());
      deleteItemPhotoPipe = sessionActionPipeCreator.createPipe(DeleteItemPhotoCommand.class, Schedulers.io());
      addBucketItemPhotoPipe = sessionActionPipeCreator.createPipe(AddBucketItemPhotoCommand.class, Schedulers.io());

      bucketListActionPipe = sessionActionPipeCreator.createPipe(BucketListCommand.class, Schedulers.io());
      findBucketItemByPhotoActionPipe = sessionActionPipeCreator.createPipe(FindBucketItemByPhotoCommand.class, Schedulers.immediate());
      uploadPhotoControllerCommandPipe = sessionActionPipeCreator.createPipe(UploadPhotoControllerCommand.class, Schedulers.immediate());
      mergeBucketItemPhotosWithStorageCommandPipe = sessionActionPipeCreator.createPipe(MergeBucketItemPhotosWithStorageCommand.class, Schedulers
            .immediate());
      recentlyAddedBucketsFromPopularCommandPipe = sessionActionPipeCreator.createPipe(RecentlyAddedBucketsFromPopularCommand.class, Schedulers
            .immediate());

      connect();
   }

   public ActionPipe<LoadBucketListFullHttpAction> loadPipe() {
      return loadBucketListPipe;
   }

   public ActionPipe<CreateBucketItemHttpAction> createPipe() {
      return createBucketPipe;
   }

   public ActionPipe<UpdateItemHttpAction> updatePipe() {
      return updateItemPipe;
   }

   public ActionPipe<ChangeOrderHttpAction> movePipe() {
      return moveItemPipe;
   }

   public ActionPipe<MarkItemAsDoneHttpAction> marksAsDonePipe() {
      return markItemAsDonePipe;
   }

   public ActionPipe<DeleteItemHttpAction> deleteItemPipe() {
      return deleteItemPipe;
   }

   public ActionPipe<DeleteItemPhotoCommand> deleteItemPhotoPipe() {
      return deleteItemPhotoPipe;
   }

   public ActionPipe<AddBucketItemPhotoCommand> addBucketItemPhotoPipe() {
      return addBucketItemPhotoPipe;
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
      createBucketPipe.observeSuccess().map(CreateBucketItemHttpAction::getResponse).subscribe(item -> {
         bucketListActionPipe.send(BucketListCommand.createItem(item));
      });
      Observable.merge(updateItemPipe.observeSuccess()
            .map(UpdateItemHttpAction::getResponse), deleteItemPhotoPipe.observeSuccess()
            .map(DeleteItemPhotoCommand::getResult), addBucketItemPhotoPipe.observeSuccess()
            .map(AddBucketItemPhotoCommand::getResult)
            .map(bucketItemBucketPhotoPair -> bucketItemBucketPhotoPair.first)).subscribe(bucketItem -> {
         bucketListActionPipe.send(BucketListCommand.updateItem(bucketItem));
      });
      markItemAsDonePipe.observeSuccess().map(MarkItemAsDoneHttpAction::getResponse).subscribe(item -> {
         bucketListActionPipe.send(BucketListCommand.markItemAsDone(item));
      });
      deleteItemPipe.observeSuccess().map(DeleteItemHttpAction::getBucketItemUid).subscribe(bucketItemUid -> {
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
