package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.photos.model.ImmutableCoordinate;
import com.worldventures.dreamtrips.api.photos.model.ImmutablePhotoUpdateParams;
import com.worldventures.dreamtrips.api.photos.model.PhotoUpdateParams;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;

import io.techery.mappery.MapperyContext;

public class PhotoUpdateParamsConverter implements Converter<UploadTask, PhotoUpdateParams> {

   @Override
   public Class<UploadTask> sourceClass() {
      return UploadTask.class;
   }

   @Override
   public Class<PhotoUpdateParams> targetClass() {
      return PhotoUpdateParams.class;
   }

   @Override
   public PhotoUpdateParams convert(MapperyContext mapperyContext, UploadTask uploadTask) {
      ImmutablePhotoUpdateParams.Builder params = ImmutablePhotoUpdateParams.builder();
      params.title(uploadTask.getTitle());
      params.shotAt(uploadTask.getShotAt());
      params.coordinate(ImmutableCoordinate.builder()
            .lat((double)uploadTask.getLatitude())
            .lng((double)uploadTask.getLongitude()).build());
      if (uploadTask.getTags() != null) {
         params.addAllTags(uploadTask.getTags());
      }
      params.locationName(uploadTask.getLocationName());
      return params.build();
   }
}
