package com.worldventures.dreamtrips.social.domain.mapping;

import com.worldventures.core.converter.Converter;
import com.worldventures.dreamtrips.api.inspirations.model.InspireMePhoto;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Inspiration;

import io.techery.mappery.MapperyContext;

public class InspirationModelsConverter implements Converter<InspireMePhoto, Inspiration> {

   @Override
   public Inspiration convert(MapperyContext mapperyContext, InspireMePhoto inspireMePhoto) {
      Inspiration inspiration = new Inspiration();
      inspiration.setId(String.valueOf(inspireMePhoto.id()));
      inspiration.setQuote(inspireMePhoto.quote());
      inspiration.setAuthor(inspireMePhoto.author());
      inspiration.setUrl(inspireMePhoto.image().url());
      return inspiration;
   }

   @Override
   public Class<InspireMePhoto> sourceClass() {
      return InspireMePhoto.class;
   }

   @Override
   public Class<Inspiration> targetClass() {
      return Inspiration.class;
   }
}
