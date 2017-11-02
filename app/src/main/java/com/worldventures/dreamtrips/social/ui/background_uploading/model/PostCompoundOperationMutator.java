package com.worldventures.dreamtrips.social.ui.background_uploading.model;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class PostCompoundOperationMutator {

   private static final int PROGRESS_MEDIA_UPLOADING = 90;
   private static final int PROGRESS_MEDIA_CREATED = 95;
   private static final int PROGRESS_POST_CREATED = 100;

   private final SessionHolder sessionSessionHolder;

   public PostCompoundOperationMutator(SessionHolder sessionSessionHolder) {
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

   public PostCompoundOperationModel failedProcessing(PostCompoundOperationModel compoundOperationModel) {
      return ImmutablePostCompoundOperationModel.copyOf(compoundOperationModel)
            .withState(CompoundOperationState.FAILED_PROCESSING);
   }

   public PostCompoundOperationModel finished(PostCompoundOperationModel compoundOperationModel, TextualPost textualPost) {
      CompoundOperationState compoundOperationState = CompoundOperationState.FINISHED;

      PostBody body;
      switch (compoundOperationModel.type()) {
         case VIDEO:
            compoundOperationState = CompoundOperationState.PROCESSING;
            textualPost.setOwner(sessionSessionHolder.get().get().user());
            body = ImmutablePostWithVideoAttachmentBody.copyOf((PostWithVideoAttachmentBody) compoundOperationModel.body())
                  .withCreatedPost(textualPost);
            break;
         case PHOTO:
            textualPost.setOwner(sessionSessionHolder.get().get().user());
            body = ImmutablePostWithPhotoAttachmentBody
                  .copyOf((PostWithPhotoAttachmentBody) compoundOperationModel.body())
                  .withCreatedPost(textualPost);
            break;
         case TEXT:
            textualPost.setOwner(sessionSessionHolder.get().get().user());
            body = ImmutableTextPostBody.copyOf((TextPostBody) compoundOperationModel.body())
                  .withCreatedPost(textualPost);
            break;
         default:
            throw new RuntimeException("Uknown type was passed");
      }

      return ImmutablePostCompoundOperationModel
            .copyOf(compoundOperationModel)
            .withProgress(PROGRESS_POST_CREATED)
            .withState(compoundOperationState)
            .withBody(body);
   }

   public PostCompoundOperationModel finishedEmpty(PostCompoundOperationModel compoundOperationModel) {
      return ImmutablePostCompoundOperationModel
            .copyOf(compoundOperationModel)
            .withProgress(PROGRESS_POST_CREATED)
            .withState(CompoundOperationState.FINISHED);
   }

   public PostCompoundOperationModel<PostWithPhotoAttachmentBody> photoAttachmentChanged(PostCompoundOperationModel<PostWithPhotoAttachmentBody> postCompoundOperationModel,
         PhotoAttachment photoAttachment, int attachmentIndex, double attachmentsUploadingProgress, long remainingTime, double averageUploadSpeed) {
      int progress = (int) (PROGRESS_MEDIA_UPLOADING * attachmentsUploadingProgress);
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

   public PostCompoundOperationModel<PostWithVideoAttachmentBody> videoAttachmentChanged(
         PostCompoundOperationModel<PostWithVideoAttachmentBody> postCompoundOperationModel,
         PostWithVideoAttachmentBody updatedPostWithVideoAttachment, int videoUploadProgress, long remainingTime, double averageUploadSpeed) {
      int progress = (PROGRESS_MEDIA_UPLOADING * videoUploadProgress) / 100;
      return ImmutablePostCompoundOperationModel
            .copyOf(postCompoundOperationModel)
            .withProgress(progress)
            .withBody(updatedPostWithVideoAttachment)
            .withMillisLeft(remainingTime)
            .withAverageUploadSpeed(averageUploadSpeed);
   }

   public PostCompoundOperationModel<PostWithPhotoAttachmentBody> photosUploaded(PostCompoundOperationModel<PostWithPhotoAttachmentBody> compoundOperationModel, List<Photo> photos) {
      return ImmutablePostCompoundOperationModel
            .copyOf(compoundOperationModel)
            .withProgress(PROGRESS_MEDIA_CREATED)
            .withBody(ImmutablePostWithPhotoAttachmentBody
                  .copyOf(compoundOperationModel.body())
                  .withUploadedPhotos(new ArrayList<>(photos)));
   }

   public PostCompoundOperationModel<PostWithVideoAttachmentBody> videoUploaded(PostCompoundOperationModel<PostWithVideoAttachmentBody> compoundOperationModel, PostWithVideoAttachmentBody body) {
      return ImmutablePostCompoundOperationModel
            .copyOf(compoundOperationModel)
            .withProgress(PROGRESS_MEDIA_UPLOADING)
            .withBody(ImmutablePostWithVideoAttachmentBody.copyOf(body)
                  .withState(PostBody.State.UPLOADED));
   }

   public PostCompoundOperationModel<PostWithVideoAttachmentBody> videoCreated(PostCompoundOperationModel<PostWithVideoAttachmentBody> compoundOperationModel, String uid) {
      return ImmutablePostCompoundOperationModel
            .copyOf(compoundOperationModel)
            .withProgress(PROGRESS_MEDIA_CREATED)
            .withBody(ImmutablePostWithVideoAttachmentBody
                  .copyOf(compoundOperationModel.body())
                  .withVideoUid(uid));
   }
}
