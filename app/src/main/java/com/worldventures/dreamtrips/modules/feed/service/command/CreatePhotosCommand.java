package com.worldventures.dreamtrips.modules.feed.service.command;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.photos.CreatePhotosHttpAction;
import com.worldventures.dreamtrips.api.photos.model.ImmutableCoordinate;
import com.worldventures.dreamtrips.api.photos.model.ImmutablePhotoCreationParams;
import com.worldventures.dreamtrips.api.photos.model.ImmutablePhotosCreationParams;
import com.worldventures.dreamtrips.api.photos.model.PhotoTagParams;
import com.worldventures.dreamtrips.api.photos.model.PhotosCreationParams;
import com.worldventures.dreamtrips.core.api.action.MappableApiActionCommand;
import com.worldventures.dreamtrips.modules.background_uploading.model.PhotoAttachment;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.Calendar;
import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CreatePhotosCommand extends MappableApiActionCommand<CreatePhotosHttpAction, List<Photo>, Photo> {

   private List<PhotoAttachment> attachments;
   private Location location;

   public CreatePhotosCommand(List<PhotoAttachment> attachments, Location location) {
      this.attachments = attachments;
      this.location = location;
   }

   @Override
   protected Class<Photo> getMappingTargetClass() {
      return Photo.class;
   }

   @Override
   protected Object mapHttpActionResult(CreatePhotosHttpAction httpAction) {
      return httpAction.response();
   }

   @Override
   protected CreatePhotosHttpAction getHttpAction() {
      return new CreatePhotosHttpAction(provideParams());
   }

   @Override
   protected Class<CreatePhotosHttpAction> getHttpActionClass() {
      return CreatePhotosHttpAction.class;
   }

   private PhotosCreationParams provideParams() {
      ImmutablePhotosCreationParams.Builder builder = ImmutablePhotosCreationParams.builder();
      Queryable.from(attachments)
            .forEachR(item -> addPhotoCreationParams(builder, item));
      return builder.build();
   }

   private void addPhotoCreationParams(ImmutablePhotosCreationParams.Builder builder, PhotoAttachment attachment) {
      ImmutablePhotoCreationParams.Builder photoParamsBuilder = ImmutablePhotoCreationParams.builder();
      photoParamsBuilder.originURL(attachment.originUrl())
            .title(attachment.selectedPhoto().title())
            .width(attachment.selectedPhoto().width())
            .height(attachment.selectedPhoto().height())
            .shotAt(Calendar.getInstance().getTime())
            .photoTags(mapperyContext.convert(attachment.selectedPhoto().tags(), PhotoTagParams.class));
      if (location != null) {
         photoParamsBuilder.coordinate(ImmutableCoordinate.builder()
               .lat(location.getLat())
               .lng(location.getLng()).build())
               .locationName(location.getName());
      }
      builder.addPhotosCreationParamsList(photoParamsBuilder.build());
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.error_fail_to_create_post;
   }
}
