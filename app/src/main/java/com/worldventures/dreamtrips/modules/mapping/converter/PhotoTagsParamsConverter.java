package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.api.photos.model.ImmutablePhotoTagParams;
import com.worldventures.dreamtrips.api.photos.model.ImmutablePhotoTagPoint;
import com.worldventures.dreamtrips.api.photos.model.ImmutableTagPosition;
import com.worldventures.dreamtrips.api.photos.model.PhotoTagParams;
import com.worldventures.dreamtrips.api.photos.model.TagPosition;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.Position;

import io.techery.mappery.MapperyContext;

public class PhotoTagsParamsConverter implements Converter<PhotoTag, PhotoTagParams> {
   @Override
   public Class<PhotoTag> sourceClass() {
      return PhotoTag.class;
   }

   @Override
   public Class<PhotoTagParams> targetClass() {
      return PhotoTagParams.class;
   }

   @Override
   public PhotoTagParams convert(MapperyContext mapperyContext, PhotoTag photoTag) {
      ImmutablePhotoTagParams.Builder tagParams = ImmutablePhotoTagParams.builder();
      tagParams.userId(photoTag.getTargetUserId());

      ImmutableTagPosition.Builder tagPosition = ImmutableTagPosition.builder();

      Position bottomRight = photoTag.getProportionalPosition().getBottomRight();
      tagPosition.bottomRight(new TagPosition.Point(bottomRight.getX(), bottomRight.getY()));

      Position topLeft = photoTag.getProportionalPosition().getTopLeft();
      tagPosition.topLeft(new TagPosition.Point(topLeft.getX(), topLeft.getY()));

      tagParams.position(tagPosition.build());

      return tagParams.build();
   }
}
