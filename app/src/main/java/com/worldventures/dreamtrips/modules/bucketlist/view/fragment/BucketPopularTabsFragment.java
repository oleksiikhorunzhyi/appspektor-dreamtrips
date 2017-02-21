package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketPopularTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.CustomViewPager;
import com.worldventures.dreamtrips.modules.common.view.bundle.BucketBundle;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.common.view.viewpager.SelectablePagerFragment;
import com.worldventures.dreamtrips.util.PageSelectionDetector;

import butterknife.InjectView;


@Layout(R.layout.fragment_popular_bucket_tab)
public class BucketPopularTabsFragment extends BaseFragmentWithArgs<BucketPopularTabsPresenter, BucketBundle>
      implements BucketPopularTabsPresenter.View {

   @InjectView(R.id.tabs) protected BadgedTabLayout tabs;
   @InjectView(R.id.pager) protected CustomViewPager pager;

   private BasePagerAdapter<FragmentItem> adapter;

   @Override
   protected BucketPopularTabsPresenter createPresenter(Bundle savedInstanceState) {
      return new BucketPopularTabsPresenter();
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);

      BucketItem.BucketType type = getArgs().getType();

      if (adapter == null) {
         this.adapter = new BasePagerAdapter<FragmentItem>(getChildFragmentManager()) {
            @Override
            public void setArgs(int position, Fragment fragment) {
               fragment.setArguments(getPresenter().getBundleForPosition(position));
            }
         };

         this.adapter.add(new FragmentItem(Route.POPULAR_BUCKET, getString(R.string.bucket_locations)));
         this.adapter.add(new FragmentItem(Route.POPULAR_BUCKET, getString(R.string.bucket_activities)));
         this.adapter.add(new FragmentItem(Route.POPULAR_BUCKET, getString(R.string.bucket_restaurants)));
      }

      pager.setAdapter(adapter);
      pager.setPagingEnabled(false);

      tabs.setupWithPagerBadged(pager);

      if (type != null) pager.setCurrentItem(type.ordinal());

      PageSelectionDetector.listenPageSelection(pager, pageNumber -> {
         SelectablePagerFragment fragment = (SelectablePagerFragment) adapter.getCurrentFragment();
         fragment.onSelectedFromPager();
      });
   }

}
