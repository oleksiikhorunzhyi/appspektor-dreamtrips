package com.worldventures.dreamtrips.modules.bucketlist;


import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketItemEditPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketListPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketPopularPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketPopularTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.ForeignBucketItemDetailsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.ForeignBucketListPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.ForeignBucketTabPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketDetailsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketItemEditFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketListPopularFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketPhotoViewPagerFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketPopularTabsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.ForeignBucketDetailsFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.ForeignBucketListFragment;
import com.worldventures.dreamtrips.modules.bucketlist.view.fragment.ForeignBucketTabsFragment;

import dagger.Module;

@Module(
      injects = {
            BucketItemEditPresenter.class,
            BucketPopularTabsPresenter.class,
            BucketItemEditFragment.class,
            BucketTabsPresenter.class,
            BucketListPresenter.class,
            BucketPopularPresenter.class,
            BucketListPopularFragment.class,
            BucketTabsFragment.class,
            BucketPopularTabsFragment.class,
            BucketListFragment.class,
            BucketDetailsFragment.class,
            BucketItemDetailsPresenter.class,
            ForeignBucketItemDetailsPresenter.class,
            ForeignBucketDetailsFragment.class,
            ForeignBucketTabsFragment.class,
            ForeignBucketTabPresenter.class,
            ForeignBucketListFragment.class,
            ForeignBucketListPresenter.class,
            BucketPhotoViewPagerFragment.class,
            BucketPhotoViewPagerFragment.Presenter.class,
      },
      complete = false,
      library = true)
public class BucketListModule {
}
