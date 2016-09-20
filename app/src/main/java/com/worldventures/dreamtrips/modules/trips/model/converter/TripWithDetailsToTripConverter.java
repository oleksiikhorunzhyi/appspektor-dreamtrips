package com.worldventures.dreamtrips.modules.trips.model.converter;

import com.worldventures.dreamtrips.api.trip.model.TripWithDetails;
import com.worldventures.dreamtrips.modules.feed.model.comment.Comment;
import com.worldventures.dreamtrips.modules.trips.model.ContentItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import io.techery.mappery.MapperyContext;

public class TripWithDetailsToTripConverter extends ApiTripToTripConverter<TripWithDetails> {

   @Override
   public Class<TripWithDetails> sourceClass() {
      return TripWithDetails.class;
   }

   @Override
   public TripModel convert(MapperyContext mapperyContext, TripWithDetails tripWithDetails) {
      TripModel tripModel = convertTrip(mapperyContext, tripWithDetails);
      tripModel.setComments(mapperyContext.convert(tripWithDetails.comments(), Comment.class));
      tripModel.setCommentsCount(tripWithDetails.commentsCount());
      tripModel.setContent(mapperyContext.convert(tripWithDetails.contentItems(), ContentItem.class));
      return tripModel;
   }
}
