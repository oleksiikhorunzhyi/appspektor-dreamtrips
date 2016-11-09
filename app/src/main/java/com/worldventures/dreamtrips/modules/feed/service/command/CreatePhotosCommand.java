package com.worldventures.dreamtrips.modules.feed.service.command;


import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.photos.CreatePhotosHttpAction;
import com.worldventures.dreamtrips.api.photos.model.ImmutableCoordinate;
import com.worldventures.dreamtrips.api.photos.model.ImmutablePhotoCreationParams;
import com.worldventures.dreamtrips.api.photos.model.ImmutablePhotosCreationParams;
import com.worldventures.dreamtrips.api.photos.model.PhotoTag;
import com.worldventures.dreamtrips.api.photos.model.PhotosCreationParams;
import com.worldventures.dreamtrips.core.api.action.MappableApiActionCommand;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.trips.model.Location;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.Calendar;
import java.util.List;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class CreatePhotosCommand extends MappableApiActionCommand<CreatePhotosHttpAction, List<Photo>, Photo> {

   private List<PhotoCreationItem> creationItems;
   private Location location;

   public CreatePhotosCommand(List<PhotoCreationItem> creationItems, Location location) {
      this.creationItems = creationItems;
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
      Queryable.from(creationItems)
            .forEachR(item -> addPhotoCreationParams(builder, item));
      return builder.build();
   }

   private void addPhotoCreationParams(ImmutablePhotosCreationParams.Builder builder, PhotoCreationItem item) {
      ImmutablePhotoCreationParams.Builder photoParamsBuilder = ImmutablePhotoCreationParams.builder();
      photoParamsBuilder.originURL(item.getOriginUrl())
            .title(item.getTitle())
            .width(item.getWidth())
            .height(item.getHeight())
            .shotAt(Calendar.getInstance().getTime())
            .photoTags(mapperyContext.convert(item.getCachedAddedPhotoTags(), PhotoTag.class));
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
