package com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.social.ui.activity.presenter.ComponentPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.ForeignBucketTabPresenter;

@Layout(R.layout.fragment_bucket_tab)
@MenuResource(R.menu.menu_mock)
@ComponentPresenter.ComponentTitle(R.string.bucket_list)
public class ForeignBucketTabsFragment extends BucketTabsFragment<ForeignBucketTabPresenter> {

   @Override
   protected ForeignBucketTabPresenter createPresenter(Bundle savedInstanceState) {
      return new ForeignBucketTabPresenter(getArgs().getUser());
   }

   @Override
   @NonNull
   protected Class<? extends BucketListFragment> getBucketRoute() {
      return ForeignBucketListFragment.class;
   }

}
