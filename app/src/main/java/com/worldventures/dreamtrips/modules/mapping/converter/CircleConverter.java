package com.worldventures.dreamtrips.modules.mapping.converter;

import com.worldventures.dreamtrips.modules.friends.model.Circle;

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
      circle.setPredefined(apiCircle.predefined());
      return circle;
   }

}
