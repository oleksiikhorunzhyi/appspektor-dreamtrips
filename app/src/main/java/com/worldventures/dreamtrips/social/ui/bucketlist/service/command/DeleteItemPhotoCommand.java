package com.worldventures.dreamtrips.social.ui.bucketlist.service.command;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.dreamtrips.api.bucketlist.DeletePhotoFromBucketItemHttpAction;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketPhoto;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DeleteItemPhotoCommand extends Command<BucketItem> implements InjectableAction {
   @Inject Janet janet;

   private BucketItem bucketItem;
   private BucketPhoto photo;

   public DeleteItemPhotoCommand(BucketItem bucketItem, BucketPhoto photo) {
      this.bucketItem = bucketItem;
      this.photo = photo;
   }

   @Override
   protected void run(CommandCallback<BucketItem> callback) throws Throwable {
      janet.createPipe(DeletePhotoFromBucketItemHttpAction.class)
            .createObservableResult(new DeletePhotoFromBucketItemHttpAction(bucketItem.getUid(), photo.getUid()))
            .map(deleteBucketPhotoAction -> {
               bucketItem.getPhotos().remove(photo);

               if (bucketItem.getCoverPhoto() != null && bucketItem.getCoverPhoto().equals(photo)) {
                  bucketItem.setCoverPhoto(bucketItem.getFirstPhoto());
               }

               return bucketItem;
            })
            .subscribe(callback::onSuccess, callback::onFail);
   }

   public BucketPhoto getPhoto() {
      return photo;
   }
}
