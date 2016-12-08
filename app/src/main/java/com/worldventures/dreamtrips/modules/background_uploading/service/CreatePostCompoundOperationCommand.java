package com.worldventures.dreamtrips.modules.background_uploading.service;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.modules.background_uploading.model.ImmutablePhotoAttachment;
import com.worldventures.dreamtrips.modules.background_uploading.model.ImmutablePostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.ImmutablePostWithAttachmentBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PhotoAttachment;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithAttachmentBody;
import com.worldventures.dreamtrips.modules.feed.model.SelectedPhoto;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import timber.log.Timber;

@CommandAction
public class CreatePostCompoundOperationCommand extends Command<PostCompoundOperationModel> {

   private String text;
   private List<SelectedPhoto> selectedPhotos;
   private Location location;

   public CreatePostCompoundOperationCommand(
         @Nullable String text,
         @Nullable List<SelectedPhoto> selectedPhotos,
         @Nullable Location location) {
      this.text = text;
      this.selectedPhotos = selectedPhotos;
      this.location = location;
   }

   @Override
   protected void run(CommandCallback<PostCompoundOperationModel> callback) throws Throwable {
      validateFields(callback);
      Observable.just(createPostCompoundOperationModel())
            .subscribe(model -> {
               Timber.d("Item was created -> {\n%s\n}", model.toString());
               callback.onSuccess(model);
            }, e -> {
               Timber.e(e, "Failed to create -> {\n%s\n}", e.getMessage());
               callback.onFail(e);
            });
   }

   private PostCompoundOperationModel createPostCompoundOperationModel() {
      return ImmutablePostCompoundOperationModel.builder()
            .id(resolveId())
            .progress(0)
            .creationDate(new Date())
            .millisLeft(0)
            .state(CompoundOperationState.SCHEDULED)
            .body(createPostBody())
            .build();
   }

   private PostWithAttachmentBody createPostBody() {
      ImmutablePostWithAttachmentBody.Builder builder = ImmutablePostWithAttachmentBody.builder();
      builder.text(text);
      builder.location(location);
      for (SelectedPhoto selectedPhoto : selectedPhotos) {
         builder.addAttachments(ImmutablePhotoAttachment.builder()
               .id(resolveId())
               .progress(0)
               .state(PhotoAttachment.State.SCHEDULED)
               .selectedPhoto(selectedPhoto)
               .build());
      }
      return builder.build();
   }

   private int resolveId() {
      return UUID.randomUUID().hashCode();
   }

   private void validateFields(CommandCallback<PostCompoundOperationModel> callback) {
      if (text == null && selectedPhotos == null) {
         callback.onFail(new IllegalStateException("Both text and attachments cannot be null"));
      }
   }
}
