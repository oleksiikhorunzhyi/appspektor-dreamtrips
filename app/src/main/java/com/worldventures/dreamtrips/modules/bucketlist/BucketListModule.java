package com.worldventures.dreamtrips.modules.bucketlist;


import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemEditPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketListPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketPopularPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketPopularTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.ForeignBucketItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.ForeignBucketListPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.ForeignBucketTabPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.AutoCompleteAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.BucketItemAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.adapter.IgnoreFirstItemAdapter;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketItemCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketItemStaticCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPhotoCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.cell.BucketPopularCell;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.BucketHorizontalPhotosView;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketDetailsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketItemEditFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListPopularFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketPopularTabsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.ForeignBucketDetailsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.ForeignBucketListFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.ForeignBucketTabsFragment;
import com.worldventures.dreamtrips.modules.common.view.horizontal_photo_view.cell.AddPhotoCell;
import com.worldventures.dreamtrips.modules.profile.adapters.IgnoreFirstExpandedItemAdapter;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.FixedListPhotosPresenter;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {BucketItemEditPresenter.class,
            BucketPopularTabsPresenter.class,
            BucketItemEditFragment.class,
            BucketTabsPresenter.class,
            BucketListPresenter.class,
            BucketPopularPresenter.class,
            BucketListPopularFragment.class,
            BucketTabsFragment.class,
            BucketPopularTabsFragment.class,
            BucketListFragment.class,
            BucketItemCell.class,
            BucketPopularCell.class,
            BucketDetailsFragment.class,
            BucketItemDetailsPresenter.class,
            AutoCompleteAdapter.class,
            BucketItemAdapter.class,
            IgnoreFirstItemAdapter.class,
            IgnoreFirstExpandedItemAdapter.class,
            AddPhotoCell.class,
            BucketPhotoCell.class,
            FixedListPhotosPresenter.class,
            BucketHorizontalPhotosView.class,
            ForeignBucketItemDetailsPresenter.class,
            ForeignBucketDetailsFragment.class,
            ForeignBucketTabsFragment.class,
            ForeignBucketTabPresenter.class,
            ForeignBucketListFragment.class,
            BucketItemStaticCell.class,
            ForeignBucketListPresenter.class
      },
      complete = false,
      library = true)
public class BucketListModule {

   public static final String ANALYTICS_DINING = "dining";
   public static final String ANALYTICS_ACTIVITIES = "activities";
   public static final String ANALYTICS_LOCATIONS = "locations";
   public static final String BUCKETLIST = Route.BUCKET_TABS.name();

   @Provides(type = Provides.Type.SET)
   ComponentDescription provideBucketListComponent() {
      return new ComponentDescription(BUCKETLIST, R.string.bucket_list, R.string.bucket_list, R.drawable.ic_bucket_lists, BucketTabsFragment.class);
   }
}
