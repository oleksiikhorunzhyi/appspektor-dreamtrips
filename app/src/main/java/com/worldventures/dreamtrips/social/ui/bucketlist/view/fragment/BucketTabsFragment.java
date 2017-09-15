package com.worldventures.dreamtrips.social.ui.bucketlist.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.social.ui.bucketlist.bundle.ForeignBucketTabsBundle;
import com.worldventures.dreamtrips.social.ui.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.social.ui.bucketlist.view.custom.CustomViewPager;
import com.worldventures.dreamtrips.modules.common.view.adapter.item.DataFragmentItem;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.SelectablePagerFragment;
import com.worldventures.dreamtrips.util.PageSelectionDetector;

import java.io.Serializable;
import java.util.List;

import butterknife.InjectView;
import icepick.State;

import static com.worldventures.dreamtrips.social.ui.bucketlist.model.BucketItem.BucketType;

@Layout(R.layout.fragment_bucket_tab)
public class BucketTabsFragment<PRESENTER extends BucketTabsPresenter> extends RxBaseFragmentWithArgs<PRESENTER, ForeignBucketTabsBundle> implements BucketTabsPresenter.View {

   @InjectView(R.id.tabs) BadgedTabLayout tabStrip;
   @InjectView(R.id.pager) CustomViewPager pager;
   BasePagerAdapter<DataFragmentItem> adapter;

   @State int currentPosition;

   @Override
   protected PRESENTER createPresenter(Bundle savedInstanceState) {
      return (PRESENTER) new BucketTabsPresenter();
   }

   @NonNull
   protected Bundle createListFragmentArgs(int position) {
      Bundle args = new Bundle();
      Serializable type = adapter.getFragmentItem(position).data;
      args.putSerializable(BucketListFragment.BUNDLE_TYPE, type);
      return args;
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      if (adapter == null) {
         adapter = new BasePagerAdapter<DataFragmentItem>(getChildFragmentManager()) {
            @Override
            public void setArgs(int position, Fragment fragment) {
               super.setArgs(position, fragment);
               Bundle args = createListFragmentArgs(position);
               fragment.setArguments(args);
            }
         };
      }

      pager.setAdapter(adapter);
      pager.setPagingEnabled(false);
      pager.setOffscreenPageLimit(2);

      PageSelectionDetector.listenPageSelection(pager, pageNumber -> {
         SelectablePagerFragment fragment = (SelectablePagerFragment) adapter.getCurrentFragment();
         fragment.onSelectedFromPager();
         notifyPosition(pageNumber);
      });
   }

   @Override
   public void onResume() {
      super.onResume();
      notifyPosition(pager.getCurrentItem());
      TrackingHelper.viewBucketListScreen();
   }

   private void notifyPosition(int currentSelectedTabPosition) {
      this.currentPosition = currentSelectedTabPosition;
      getPresenter().onTabChange(BucketType.values()[currentPosition]);
   }

   @Override
   public void updateSelection() {
      pager.setCurrentItem(currentPosition);
   }

   @Override
   public void setTypes(List<BucketType> types) {
      if (adapter.getCount() == 0) {
         for (BucketType type : types) {
            adapter.add(new DataFragmentItem<>(getBucketRoute(), getString(type.getRes()), type));
         }
         adapter.notifyDataSetChanged();
      }
      //
      tabStrip.setupWithPagerBadged(pager);
   }

   @NonNull
   protected Route getBucketRoute() {
      return Route.BUCKET_LIST;
   }

   @Override
   public void setRecentBucketItemCountByType(BucketType type, int count) {
      tabStrip.setBadgeCount(type.ordinal(), count);
   }
}
