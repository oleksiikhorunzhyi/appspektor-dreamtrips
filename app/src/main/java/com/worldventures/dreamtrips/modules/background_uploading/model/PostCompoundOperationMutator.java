package com.worldventures.dreamtrips.modules.background_uploading.model;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class PostCompoundOperationMutator {

   private static final int PROGRESS_PHOTOS_CREATING = 90;
   private static final int PROGRESS_MEDIA_CREATED = 95;
   private static final int PROGRESS_POST_CREATED = 100;

   private SessionHolder<UserSession> sessionSessionHolder;

   public PostCompoundOperationMutator(SessionHolder<UserSession> sessionSessionHolder) {
      this.sessionSessionHolder = sessionSessionHolder;
   }

   public PostCompoundOperationModel start(PostCompoundOperationModel compoundOperationModel) {
      return ImmutablePostCompoundOperationModel.copyOf(compoundOperationModel)
            .withState(CompoundOperationState.STARTED);
   }

   public PostCompoundOperationModel pause(PostCompoundOperationModel compoundOperationModel) {
      return ImmutablePostCompoundOperationModel.copyOf(compoundOperationModel)
            .withState(CompoundOperationState.PAUSED);
   }

   public PostCompoundOperationModel resume(PostCompoundOperationModel compoundOperationModel) {
      return ImmutablePostCompoundOperationModel.copyOf(compoundOperationModel)
            .withState(CompoundOperationState.SCHEDULED)
            .withCreationDate(new Date());
   }

   public PostCompoundOperationModel failed(PostCompoundOperationModel compoundOperationModel) {
      return ImmutablePostCompoundOperationModel.copyOf(compoundOperationModel)
            .withState(CompoundOperationState.FAILED);
   }

   public PostCompoundOperationModel finished(PostCompoundOperationModel compoundOperationModel, TextualPost textualPost) {
      textualPost.setOwner(sessionSessionHolder.get().get().getUser());

      PostBody body;
      switch (compoundOperationModel.type()) {
         case VIDEO:
            body = ImmutablePostWithVideoAttachmentBody.copyOf((PostWithVideoAttachmentBody) compoundOperationModel.body())
                  .withCreatedPost(textualPost);
            break;
         case PHOTO:
            body = ImmutablePostWithPhotoAttachmentBody
                  .copyOf((PostWithPhotoAttachmentBody) compoundOperationModel.body())
                  .withCreatedPost(textualPost);
            break;
         case TEXT:
            body = ImmutableTextPostBody.copyOf((TextPostBody) compoundOperationModel.body())
                  .withCreatedPost(textualPost);
            break;
         default:
            throw new RuntimeException("Uknown type was passed");
      }

      return ImmutablePostCompoundOperationModel
            .copyOf(compoundOperationModel)
            .withProgress(PROGRESS_POST_CREATED)
            .withState(CompoundOperationState.FINISHED)
            .withBody(body);
   }

   public PostCompoundOperationModel<PostWithPhotoAttachmentBody> photoAttachmentChanged(PostCompoundOperationModel<PostWithPhotoAttachmentBody> postCompoundOperationModel,
         PhotoAttachment photoAttachment, int attachmentIndex, double attachmentsUploadingProgress, long remainingTime, double averageUploadSpeed) {
      int progress = (int) (PROGRESS_PHOTOS_CREATING * attachmentsUploadingProgress);
      Timber.d("Post uploading progress - %d of 100", progress);
      List<PhotoAttachment> attachments = new ArrayList<>(postCompoundOperationModel.body().attachments());
      attachments.remove(attachmentIndex);
      attachments.add(attachmentIndex, photoAttachment);
      return ImmutablePostCompoundOperationModel
            .copyOf(postCompoundOperationModel)
            .withProgress(progress)
            .withMillisLeft(remainingTime)
            .withAverageUploadSpeed(averageUploadSpeed)
            .withBody(ImmutablePostWithPhotoAttachmentBody
                  .copyOf(postCompoundOperationModel.body())
                  .withAttachments(new ArrayList<>(attachments)));
   }

   public PostCompoundOperationModel<PostWithPhotoAttachmentBody> photosUploaded(PostCompoundOperationModel<PostWithPhotoAttachmentBody> compoundOperationModel, List<Photo> photos) {
      return ImmutablePostCompoundOperationModel
            .copyOf(compoundOperationModel)
            .withProgress(PROGRESS_MEDIA_CREATED)
            .withBody(ImmutablePostWithPhotoAttachmentBody
                  .copyOf(compoundOperationModel.body())
                  .withUploadedPhotos(new ArrayList<>(photos)));
   }

   public PostCompoundOperationModel<PostWithVideoAttachmentBody> videoAttachmentChanged(
         PostCompoundOperationModel<PostWithVideoAttachmentBody> postCompoundOperationModel,
         PostWithVideoAttachmentBody body,
         int uploadingProgress, long remainingTime, double averageUploadSpeed) {
      return ImmutablePostCompoundOperationModel
            .copyOf(postCompoundOperationModel)
            .withBody(body)
            .withProgress(uploadingProgress)
            .withMillisLeft(remainingTime)
            .withAverageUploadSpeed(averageUploadSpeed);
   }

}
