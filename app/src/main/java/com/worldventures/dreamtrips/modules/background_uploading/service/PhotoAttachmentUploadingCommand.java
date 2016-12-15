package com.worldventures.dreamtrips.modules.background_uploading.service;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.background_uploading.model.ImmutablePhotoAttachment;
import com.worldventures.dreamtrips.modules.background_uploading.model.PhotoAttachment;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationMutator;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

@CommandAction
public class PhotoAttachmentUploadingCommand extends Command<PostCompoundOperationModel> implements InjectableAction {

   @Inject Janet janet;
   @Inject PostCompoundOperationMutator compoundOperationObjectMutator;

   private PostCompoundOperationModel postCompoundOperationModel;
   private PhotoAttachment photoAttachment;

   private double totalUploadedSize;
   private double totalSize;
   private int attachmentIndex;

   public PhotoAttachmentUploadingCommand(PostCompoundOperationModel postCompoundOperationModel,
         PhotoAttachment photoAttachment) {
      this.postCompoundOperationModel = postCompoundOperationModel;
      this.photoAttachment = photoAttachment;
      attachmentIndex = postCompoundOperationModel.body().attachments().indexOf(photoAttachment);
      calculateSize();
   }

   private void calculateSize() {
      Queryable<PhotoAttachment> uploadedQueryable = Queryable.from(postCompoundOperationModel.body().attachments())
            .filter(element -> element.state() == PhotoAttachment.State.UPLOADED);
      if (uploadedQueryable != null && uploadedQueryable.count() != 0) {
         totalUploadedSize = uploadedQueryable.map(item -> item.selectedPhoto().size()).sum();
      }
      totalSize = Queryable.from(postCompoundOperationModel.body().attachments())
            .map(item -> item.selectedPhoto().size()).sum();
   }

   @Override
   protected void run(CommandCallback<PostCompoundOperationModel> callback) throws Throwable {
      janet.createPipe(SimpleUploaderyCommand.class)
            .createObservable(new SimpleUploaderyCommand("file://" + photoAttachment.selectedPhoto().path()))
            .throttleLast(10, TimeUnit.MILLISECONDS)
            .map(actionState -> {
               ImmutablePhotoAttachment.Builder builder = ImmutablePhotoAttachment.builder()
                     .from(photoAttachment);
               switch (actionState.status) {
                  case START:
                     Timber.d("[New Photo Attachment Creation] Uploading photo %s", photoAttachment.selectedPhoto()
                           .title());
                     builder.state(PhotoAttachment.State.STARTED);
                     break;
                  case PROGRESS:
                     Timber.d("[New Photo Attachment Creation] In progress %d", actionState.progress);
                     builder.progress(actionState.progress);
                     break;
                  case SUCCESS:
                     String originUrl = actionState.action.getResult().response().uploaderyPhoto().location();
                     Timber.d("[New Photo Attachment Creation] Succeed %s", originUrl);
                     builder.originUrl(originUrl);
                     builder.progress(100);
                     builder.state(PhotoAttachment.State.UPLOADED);
                     break;
                  case FAIL:
                     Timber.e(actionState.exception, "[New Photo Attachment Creation] Upload failed");
                     builder.state(PhotoAttachment.State.FAILED);
                     break;
               }
               photoAttachmentUpdated(builder.build());
               return actionState;
            })
            .subscribe(new ActionStateSubscriber<SimpleUploaderyCommand>()
                  .onProgress((command, progress) -> callback.onProgress(progress))
                  .onSuccess(command -> callback.onSuccess(postCompoundOperationModel))
                  .onFail((command, e) -> callback.onFail(e)));
   }

   PostCompoundOperationModel getPostCompoundOperationModel() {
      return postCompoundOperationModel;
   }

   private void photoAttachmentUpdated(PhotoAttachment updatedAttachment) {
      photoAttachment = updatedAttachment;
      postCompoundOperationModel = compoundOperationObjectMutator.photoAttachmentChanged(postCompoundOperationModel,
            photoAttachment, attachmentIndex, (totalUploadedSize + getUploadedSizeOfAttachment(updatedAttachment)) / totalSize);
   }

   private double getUploadedSizeOfAttachment(PhotoAttachment updatedAttachment) {
      double uplodedSize = updatedAttachment.progress() * updatedAttachment.selectedPhoto().size() / 100.0d;
      Timber.d("Photo attachment uploading - %d of %d", (long) uplodedSize, updatedAttachment.selectedPhoto().size());
      return uplodedSize;
   }
}
