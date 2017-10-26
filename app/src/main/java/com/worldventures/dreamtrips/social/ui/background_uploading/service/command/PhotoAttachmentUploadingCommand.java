package com.worldventures.dreamtrips.social.ui.background_uploading.service.command;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.dreamtrips.core.api.uploadery.SimpleUploaderyCommand;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.ImmutablePhotoAttachment;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PhotoAttachment;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostBody;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationMutator;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostWithPhotoAttachmentBody;
import com.worldventures.dreamtrips.social.ui.background_uploading.util.UploadTimeEstimator;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

@CommandAction
public class PhotoAttachmentUploadingCommand extends Command<PostCompoundOperationModel<PostWithPhotoAttachmentBody>> implements InjectableAction {

   @Inject Janet janet;
   @Inject PostCompoundOperationMutator compoundOperationObjectMutator;
   @Inject UploadTimeEstimator uploadTimeEstimator;

   private PostCompoundOperationModel<PostWithPhotoAttachmentBody> postCompoundOperationModel;
   private PhotoAttachment photoAttachment;

   private double totalUploadedSize;
   private double totalSize;
   private int attachmentIndex;

   public PhotoAttachmentUploadingCommand(PostCompoundOperationModel<PostWithPhotoAttachmentBody> postCompoundOperationModel,
         PhotoAttachment photoAttachment) {
      this.postCompoundOperationModel = postCompoundOperationModel;
      this.photoAttachment = photoAttachment;
      attachmentIndex = postCompoundOperationModel.body().attachments().indexOf(photoAttachment);
      calculateSize();
   }

   private void calculateSize() {
      Queryable<PhotoAttachment> uploadedQueryable = Queryable.from(postCompoundOperationModel.body().attachments())
            .filter(element -> element.state() == PostBody.State.UPLOADED);
      if (uploadedQueryable != null && uploadedQueryable.count() != 0) {
         totalUploadedSize = uploadedQueryable.map(item -> item.selectedPhoto().size()).sum();
      }
      totalSize = Queryable.from(postCompoundOperationModel.body().attachments())
            .map(item -> item.selectedPhoto().size()).sum();
   }

   @Override
   protected void run(CommandCallback<PostCompoundOperationModel<PostWithPhotoAttachmentBody>> callback) throws Throwable {
      janet.createPipe(SimpleUploaderyCommand.class)
            .createObservable(new SimpleUploaderyCommand("file://" + photoAttachment.selectedPhoto().path()))
            .throttleLast(10, TimeUnit.MILLISECONDS)
            .map(actionState -> {
               ImmutablePhotoAttachment.Builder builder = ImmutablePhotoAttachment.builder()
                     .from(photoAttachment);
               switch (actionState.status) {
                  case START:
                     uploadTimeEstimator.prepare(totalSize, totalUploadedSize, photoAttachment.selectedPhoto().size(),
                           postCompoundOperationModel.averageUploadSpeed());
                     uploadTimeEstimator.onUploadingStarted(System.currentTimeMillis());
                     Timber.d("[New Photo Attachment Creation] Uploading photo %s", photoAttachment.selectedPhoto()
                           .title());
                     builder.state(PostBody.State.STARTED);
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
                     builder.state(PostBody.State.UPLOADED);
                     break;
                  case FAIL:
                     Timber.e(actionState.exception, "[New Photo Attachment Creation] Upload failed");
                     builder.state(PostBody.State.FAILED);
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
      long remainingTimeInMillis = uploadTimeEstimator.estimate(updatedAttachment.progress(), System.currentTimeMillis());
      double updatedAverageSpeed = uploadTimeEstimator.getAverageUploadSpeed();
      postCompoundOperationModel = compoundOperationObjectMutator.photoAttachmentChanged(postCompoundOperationModel,
            photoAttachment, attachmentIndex, (totalUploadedSize + getUploadedSizeOfAttachment(updatedAttachment)) / totalSize,
            remainingTimeInMillis, updatedAverageSpeed);
   }

   private double getUploadedSizeOfAttachment(PhotoAttachment updatedAttachment) {
      double uplodedSize = updatedAttachment.progress() * updatedAttachment.selectedPhoto().size() / 100.0d;
      Timber.d("Photo attachment uploading - %d of %d", (long) uplodedSize, updatedAttachment.selectedPhoto().size());
      return uplodedSize;
   }
}
