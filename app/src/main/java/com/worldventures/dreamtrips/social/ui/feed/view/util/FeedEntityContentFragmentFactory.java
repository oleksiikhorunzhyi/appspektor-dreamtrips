package com.worldventures.dreamtrips.social.ui.feed.view.util;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Pair;

import com.worldventures.core.model.User;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.dreamtrips.core.navigation.creator.BucketDetailsFragmentClassProvider;
import com.worldventures.dreamtrips.core.navigation.creator.FragmentClassProvider;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripDetailsBundle;
import com.worldventures.dreamtrips.modules.trips.view.fragment.TripDetailsFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.BucketBundle;
import com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.social.ui.feed.model.FeedEntityHolder;

public class FeedEntityContentFragmentFactory {

   private final FragmentClassProvider bucketFragmentClassProvider;

   public FeedEntityContentFragmentFactory(SessionHolder sessionHolder) {
      this.bucketFragmentClassProvider = new BucketDetailsFragmentClassProvider(sessionHolder);
   }

   public Pair<Class<? extends Fragment>, Parcelable> create(FeedEntityHolder holder) {
      Class<? extends Fragment> routeClazz = null;
      Parcelable args = null;
      switch (holder.getType()) {
         case UNDEFINED:
            //now is not used.
            break;
         case TRIP:
            routeClazz = TripDetailsFragment.class;
            args = new TripDetailsBundle((TripModel) holder.getItem());
            break;
         case BUCKET_LIST_ITEM:
            User user = holder.getItem().getOwner();
            int userId = user != null ? user.getId() : 0;
            routeClazz = bucketFragmentClassProvider.provideFragmentClass(userId);
            BucketBundle bucketBundle = new BucketBundle();
            BucketItem item = (BucketItem) holder.getItem();
            bucketBundle.setType(item.getType());
            bucketBundle.setBucketItem(item);
            bucketBundle.setOwnerId(userId);
            args = bucketBundle;
            break;
         default:
            break;
      }
      return new Pair<>(routeClazz, args);
   }
}
