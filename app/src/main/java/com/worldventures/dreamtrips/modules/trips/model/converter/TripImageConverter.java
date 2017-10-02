package com.worldventures.dreamtrips.modules.trips.model.converter;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.trip.model.TripImage;

import io.techery.mappery.MapperyContext;

public class TripImageConverter implements Converter<TripImage, com.worldventures.dreamtrips.social.ui.tripsimages.model.TripImage> {
   @Override
   public Class<TripImage> sourceClass() {
      return TripImage.class;
   }

   @Override
   public Class<com.worldventures.dreamtrips.social.ui.tripsimages.model.TripImage> targetClass() {
      return com.worldventures.dreamtrips.social.ui.tripsimages.model.TripImage.class;
   }

   @Override
   public com.worldventures.dreamtrips.social.ui.tripsimages.model.TripImage convert(MapperyContext mapperyContext, TripImage sourceTripImage) {
      com.worldventures.dreamtrips.social.ui.tripsimages.model.TripImage tripImage = new com.worldventures.dreamtrips.social.ui.tripsimages.model.TripImage();
      tripImage.setId(sourceTripImage.id());
      tripImage.setUrl(sourceTripImage.url());
      tripImage.setOriginUrl(sourceTripImage.originUrl());
      tripImage.setDescription(sourceTripImage.description());
      tripImage.setType(sourceTripImage.type());
      return tripImage;
   }
}
