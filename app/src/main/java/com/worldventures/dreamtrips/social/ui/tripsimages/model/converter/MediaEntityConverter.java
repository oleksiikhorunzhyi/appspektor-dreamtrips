package com.worldventures.dreamtrips.social.ui.tripsimages.model.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.entity.model.EntityHolder;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.BaseMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.PhotoMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.TripImageType;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.UndefinedMediaEntity;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.VideoMediaEntity;

import io.techery.mappery.MapperyContext;

public class MediaEntityConverter implements Converter<EntityHolder, BaseMediaEntity> {

   @Override
   public Class<EntityHolder> sourceClass() {
      return EntityHolder.class;
   }

   @Override
   public Class<BaseMediaEntity> targetClass() {
      return BaseMediaEntity.class;
   }

   @Override
   public BaseMediaEntity convert(MapperyContext mapperyContext, EntityHolder entityHolder) {
      TripImageType type = TripImageType.from(entityHolder.type());
      Class<? extends FeedEntity> targetClass = null;
      switch (type) {
         case PHOTO:
            targetClass = Photo.class;
            break;
         case VIDEO:
            targetClass = Video.class;
            break;
      }

      BaseMediaEntity baseMediaEntity = new UndefinedMediaEntity();
      switch (type) {
         case PHOTO:
            baseMediaEntity = new PhotoMediaEntity();
            break;
         case VIDEO:
            baseMediaEntity = new VideoMediaEntity();
            break;
      }
      baseMediaEntity.setItem(mapperyContext.convert(entityHolder.entity(), targetClass));
      return baseMediaEntity;
   }

}