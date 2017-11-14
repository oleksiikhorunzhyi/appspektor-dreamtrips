package com.worldventures.dreamtrips.social.ui.background_uploading.service.command;

import com.worldventures.janet.injection.InjectableAction;
import com.worldventures.dreamtrips.social.ui.background_uploading.VideoMicroserviceModule;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.ImmutablePostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostBody;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostCompoundOperationMutator;
import com.worldventures.dreamtrips.social.ui.background_uploading.model.PostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.video.http.UploadVideoHttpAction;
import com.worldventures.dreamtrips.social.ui.background_uploading.util.UploadTimeEstimator;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.helper.ActionStateSubscriber;
import timber.log.Timber;

@CommandAction
public class UploadVideoFileCommand extends Command<PostCompoundOperationModel<PostWithVideoAttachmentBody>> implements InjectableAction {

   @Inject @Named(VideoMicroserviceModule.JANET_QUALIFIER) Janet janet;
   @Inject PostCompoundOperationMutator compoundOperationObjectMutator;
   @Inject UploadTimeEstimator uploadTimeEstimator;

   private PostCompoundOperationModel<PostWithVideoAttachmentBody> postCompoundOperationModel;

   private double totalSize;
   private double totalUploadedSize;

   private long uploadStartedTimeStamp;

   public UploadVideoFileCommand(PostCompoundOperationModel<PostWithVideoAttachmentBody> postCompoundOperationModel) {
      this.postCompoundOperationModel = postCompoundOperationModel;
      this.totalSize = postCompoundOperationModel.body().size();
      this.totalUploadedSize = 0;
      this.uploadStartedTimeStamp = new Date().getTime();
   }

   @Override
   protected void run(CommandCallback<PostCompoundOperationModel<PostWithVideoAttachmentBody>> callback) throws Throwable {
      janet.createPipe(UploadVideoHttpAction.class)
            .createObservable(new UploadVideoHttpAction(postCompoundOperationModel.body().videoPath()))
            .throttleLast(100, TimeUnit.MILLISECONDS)
            .map(actionState -> {
               ImmutablePostWithVideoAttachmentBody.Builder builder = ImmutablePostWithVideoAttachmentBody.builder()
                     .from(postCompoundOperationModel.body());
               int progress = 0;
               switch (actionState.status) {
                  case START:
                     uploadTimeEstimator.prepare(totalSize, totalUploadedSize,
                           postCompoundOperationModel.body().size(),
                           postCompoundOperationModel.averageUploadSpeed());
                     uploadTimeEstimator.onUploadingStarted(System.currentTimeMillis());
                     builder.state(PostBody.State.STARTED);
                     break;
                  case PROGRESS:
                     Timber.d("[New Photo Attachment Creation] In progress %d", actionState.progress);
                     progress = actionState.progress;
                     break;
                  case SUCCESS:
                     String uploadId = actionState.action.getAssetId();
                     Timber.d("[New Photo Attachment Creation] Succeed %s", uploadId);
                     progress = 100;
                     builder.uploadTime((new Date().getTime() - uploadStartedTimeStamp) / 1000);
                     builder.state(PostBody.State.UPLOADED);
                     builder.uploadId(uploadId);
                     break;
                  case FAIL:
                     Timber.e(actionState.exception, "[New Photo Attachment Creation] Upload failed");
                     builder.state(PostBody.State.FAILED);
                     break;
                  default:
                     break;
               }
               long remainingTimeInMillis = uploadTimeEstimator.estimate(progress, System.currentTimeMillis());
               double updatedAverageSpeed = uploadTimeEstimator.getAverageUploadSpeed();
               videoAttachmentUpdated(builder.build(), progress, remainingTimeInMillis, updatedAverageSpeed);
               return actionState;
            })
            .subscribe(new ActionStateSubscriber<UploadVideoHttpAction>()
                  .onProgress((command, progress) -> callback.onProgress(progress))
                  .onSuccess(command -> callback.onSuccess(postCompoundOperationModel))
                  .onFail((command, e) -> callback.onFail(e)));
   }

   private void videoAttachmentUpdated(PostWithVideoAttachmentBody updatedAttachment, int progress,
         long remainingTimeInMillis, double updatedAverageSpeed) {
      postCompoundOperationModel = compoundOperationObjectMutator.videoAttachmentChanged(postCompoundOperationModel,
            updatedAttachment, progress, remainingTimeInMillis, updatedAverageSpeed);
   }

   public PostCompoundOperationModel<PostWithVideoAttachmentBody> getPostCompoundOperationModel() {
      return postCompoundOperationModel;
   }
}
