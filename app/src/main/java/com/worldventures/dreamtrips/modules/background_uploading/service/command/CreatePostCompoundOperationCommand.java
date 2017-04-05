package com.worldventures.dreamtrips.modules.background_uploading.service.command;

import android.media.MediaMetadataRetriever;
import android.support.annotation.Nullable;

import com.innahema.collections.query.functions.Converter;
import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.utils.FileUtils;
import com.worldventures.dreamtrips.modules.background_uploading.model.CompoundOperationState;
import com.worldventures.dreamtrips.modules.background_uploading.model.ImmutablePhotoAttachment;
import com.worldventures.dreamtrips.modules.background_uploading.model.ImmutablePostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.ImmutablePostWithPhotoAttachmentBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.ImmutablePostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.ImmutableTextPostBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostCompoundOperationModel;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithPhotoAttachmentBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.PostWithVideoAttachmentBody;
import com.worldventures.dreamtrips.modules.background_uploading.model.TextPostBody;
import com.worldventures.dreamtrips.modules.feed.bundle.CreateEntityBundle;
import com.worldventures.dreamtrips.modules.feed.model.ImmutableSelectedPhoto;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.feed.model.SelectedPhoto;
import com.worldventures.dreamtrips.modules.trips.model.Location;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import timber.log.Timber;

import static com.worldventures.dreamtrips.modules.background_uploading.model.PostBody.Type.PHOTO;
import static com.worldventures.dreamtrips.modules.background_uploading.model.PostBody.Type.TEXT;
import static com.worldventures.dreamtrips.modules.background_uploading.model.PostBody.Type.VIDEO;

@CommandAction
public class CreatePostCompoundOperationCommand extends Command<PostCompoundOperationModel<PostBody>> {

   private @Nullable String text;
   private @Nullable List<SelectedPhoto> selectedPhotos;
   private @Nullable String selectedVideoPath;
   private @Nullable Location location;
   private final CreateEntityBundle.Origin origin;

   public CreatePostCompoundOperationCommand(@Nullable String text, @Nullable List<SelectedPhoto> selectedPhotos,
         CreateEntityBundle.Origin origin, @Nullable Location location) {
      this.text = text;
      this.selectedPhotos = selectedPhotos;
      this.location = location;
      this.origin = origin;
   }

   public CreatePostCompoundOperationCommand(@Nullable String text, @Nullable List<PhotoCreationItem> selectedPhotos,
         @Nullable String selectedVideoPath, @Nullable Location location, CreateEntityBundle.Origin origin) {
      this.text = text;
      this.selectedPhotos = getSelectionPhotos(selectedPhotos);
      this.selectedVideoPath = selectedVideoPath;
      this.location = location;
      this.origin = origin;
   }

   @Override
   protected void run(CommandCallback<PostCompoundOperationModel<PostBody>> callback) throws Throwable {
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

   private PostCompoundOperationModel<PostBody> createPostCompoundOperationModel() {
      PostBody.Type type = obtainType();
      return ImmutablePostCompoundOperationModel.builder()
            .id(resolveId())
            .progress(0)
            .creationDate(new Date())
            .millisLeft(0)
            .averageUploadSpeed(0)
            .type(type)
            .state(CompoundOperationState.SCHEDULED)
            .body(createBody(type))
            .build();
   }

   private PostBody.Type obtainType() {
      if (selectedVideoPath != null) return VIDEO;
      if (selectedPhotos != null && !selectedPhotos.isEmpty()) return PHOTO;
      return TEXT;
   }

   private PostBody createBody(PostBody.Type type) {
      if (type == PHOTO) return createPostPhotoBody();
      if (type == VIDEO) return createPostVideoBody();
      return createPostBody();
   }

   private PostWithPhotoAttachmentBody createPostPhotoBody() {
      ImmutablePostWithPhotoAttachmentBody.Builder builder = ImmutablePostWithPhotoAttachmentBody.builder();
      builder.text(text);
      builder.origin(origin);
      builder.location(location);
      builder.attachments(new ArrayList<>(Queryable.from(selectedPhotos)
            .map(selectedPhoto -> ImmutablePhotoAttachment.builder()
                  .id(resolveId())
                  .progress(0)
                  .state(PostBody.State.SCHEDULED)
                  .selectedPhoto(selectedPhoto)
                  .build()).toList()));
      return builder.build();
   }

   private PostWithVideoAttachmentBody createPostVideoBody() {
      return ImmutablePostWithVideoAttachmentBody.builder()
            .text(text)
            .origin(origin)
            .location(location)
            .videoPath(selectedVideoPath)
            .state(PostBody.State.SCHEDULED)
            .aspectRatio(obtainAspectRatio(selectedVideoPath))
            .build();
   }

   private TextPostBody createPostBody() {
      return ImmutableTextPostBody.builder()
            .text(text)
            .origin(origin)
            .location(location)
            .build();
   }

   private int resolveId() {
      return UUID.randomUUID().hashCode();
   }

   private void validateFields(CommandCallback<PostCompoundOperationModel<PostBody>> callback) {
      if (text == null && selectedPhotos == null && selectedVideoPath == null) {
         callback.onFail(new IllegalStateException("Both text and attachments cannot be null"));
      }
   }

   private List<SelectedPhoto> getSelectionPhotos(List<PhotoCreationItem> items) {
      return Queryable.from(items)
            .map((Converter<PhotoCreationItem, SelectedPhoto>) element ->
                  ImmutableSelectedPhoto.builder()
                        .title(element.getTitle())
                        .path(element.getFilePath())
                        .locationFromExif(element.getLocationFromExif())
                        .tags(element.getCachedAddedPhotoTags())
                        .locationFromPost(location)
                        .source(element.getSource())
                        .size(FileUtils.getFileSize(element.getFilePath()))
                        .width(element.getWidth())
                        .height(element.getHeight())
                        .build())
            .toList();
   }

   public static double obtainAspectRatio(String filePath) {
      MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
      metaRetriever.setDataSource(filePath);
      int rotation = Integer.valueOf(metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
      String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
      String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);

      return (rotation == 90 || rotation == 270)
            ? Double.parseDouble(height) / Double.parseDouble(width)
            : Double.parseDouble(width) / Double.parseDouble(height);
   }
}
