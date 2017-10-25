package com.worldventures.dreamtrips.social.domain.mapping;

import com.worldventures.core.converter.Converter;
import com.worldventures.core.model.Circle;

import io.techery.mappery.MapperyContext;

public class CircleConverter implements Converter<com.worldventures.dreamtrips.api.circles.model.Circle, Circle> {

   @Override
   public Class<com.worldventures.dreamtrips.api.circles.model.Circle> sourceClass() {
      return com.worldventures.dreamtrips.api.circles.model.Circle.class;
   }

   @Override
   public Class<Circle> targetClass() {
      return Circle.class;
   }

   @Override
   public Circle convert(MapperyContext mapperyContext, com.worldventures.dreamtrips.api.circles.model.Circle apiCircle) {
      Circle circle = new Circle();
      circle.setId(apiCircle.id());
      circle.setName(apiCircle.name());
      return circle;
   }

}
