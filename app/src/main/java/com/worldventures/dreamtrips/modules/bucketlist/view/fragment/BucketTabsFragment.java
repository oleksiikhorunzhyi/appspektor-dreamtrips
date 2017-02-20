package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.bucketlist.bundle.ForeignBucketTabsBundle;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.CustomViewPager;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.common.view.adapter.item.DataFragmentItem;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;

import java.io.Serializable;
import java.util.List;

import butterknife.InjectView;
import icepick.State;
import rx.Observable;

import static com.worldventures.dreamtrips.modules.bucketlist.model.BucketItem.BucketType;

@Layout(R.layout.fragment_bucket_tab)
public class BucketTabsFragment<PRESENTER extends BucketTabsPresenter> extends RxBaseFragmentWithArgs<PRESENTER, ForeignBucketTabsBundle> implements BucketTabsPresenter.View, ApiErrorView {

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

      pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         @Override
         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
         }

         @Override
         public void onPageSelected(int position) {
            currentPosition = position;
            notifyPosition();
         }

         @Override
         public void onPageScrollStateChanged(int state) {
         }
      });
   }

   @Override
   public void onResume() {
      super.onResume();
      notifyPosition();
      TrackingHelper.viewBucketListScreen();
   }

   private void notifyPosition() {
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

   @Override
   public <T> Observable<T> bind(Observable<T> observable) {
      return observable;
   }

   @Override
   public boolean onApiError(ErrorResponse errorResponse) {
      return false;
   }

   @Override
   public void onApiCallFailed() {

   }
}
