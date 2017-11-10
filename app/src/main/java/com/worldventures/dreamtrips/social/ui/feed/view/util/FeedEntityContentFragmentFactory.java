package com.worldventures.dreamtrips.social.ui.feed.view.util;

import android.os.Parcelable;
import android.util.Pair;

import com.worldventures.core.model.User;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.creator.BucketDetailsRouteCreator;
import com.worldventures.dreamtrips.core.navigation.creator.RouteCreator;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripDetailsBundle;
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.BucketBundle;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;

public class FeedEntityContentFragmentFactory {

   private final RouteCreator bucketRouteCreator;

   public FeedEntityContentFragmentFactory(SessionHolder sessionHolder) {
      this.bucketRouteCreator = new BucketDetailsRouteCreator(sessionHolder);
   }

   public Pair<Route, Parcelable> create(FeedEntityHolder holder) {
      FeedEntityHolder.Type type = holder.getType();

      Route route = null;
      Parcelable args = null;
      switch (type) {
         case UNDEFINED:
            //now is not used.
            break;
         case TRIP:
            route = Route.DETAILED_TRIP;
            args = new TripDetailsBundle((TripModel) holder.getItem());
            break;
         case BUCKET_LIST_ITEM:
            User user = holder.getItem().getOwner();
            int userId = user != null ? user.getId() : 0;
            route = bucketRouteCreator.createRoute(userId);
            BucketBundle bucketBundle = new BucketBundle();
            BucketItem item = (BucketItem) holder.getItem();
            bucketBundle.setType(item.getType());
            bucketBundle.setBucketItem(item);
            bucketBundle.setOwnerId(userId);
            args = bucketBundle;
            break;
      }
      return new Pair<>(route, args);
   }
}