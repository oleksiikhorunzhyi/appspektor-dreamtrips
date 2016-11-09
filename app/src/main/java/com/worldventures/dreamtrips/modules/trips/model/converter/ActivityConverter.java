package com.worldventures.dreamtrips.modules.trips.model.converter;

import com.worldventures.dreamtrips.api.trip.model.TripActivity;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;

import io.techery.mappery.MapperyContext;

public class ActivityConverter implements Converter<TripActivity, ActivityModel> {
   @Override
   public Class<TripActivity> sourceClass() {
      return TripActivity.class;
   }

   @Override
   public Class<ActivityModel> targetClass() {
      return ActivityModel.class;
   }

   @Override
   public ActivityModel convert(MapperyContext mapperyContext, TripActivity tripActivity) {
      ActivityModel activityModel = new ActivityModel();
      activityModel.setId(tripActivity.id());
      activityModel.setParentId(tripActivity.parentId());
      activityModel.setPosition(tripActivity.position());
      activityModel.setIcon(tripActivity.icon());
      activityModel.setName(tripActivity.name());
      return activityModel;
   }
}
