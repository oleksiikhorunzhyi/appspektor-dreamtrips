package com.worldventures.dreamtrips.social.ui.bucketlist;


import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.BucketItemDetailsPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.BucketItemEditPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.BucketListPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.BucketPopularPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.BucketPopularTabsPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.ForeignBucketItemDetailsPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.ForeignBucketListPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.ForeignBucketTabPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketDetailsFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketItemEditFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketListFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketListPopularFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketPhotoViewPagerFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketPopularTabsFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.BucketTabsFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.ForeignBucketDetailsFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.ForeignBucketListFragment;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment.ForeignBucketTabsFragment;

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
