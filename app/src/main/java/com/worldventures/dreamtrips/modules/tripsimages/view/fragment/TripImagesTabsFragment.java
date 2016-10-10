package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesTabsPresenter;

import butterknife.InjectView;

@Layout(R.layout.fragment_trip_images_tabs)
@MenuResource(R.menu.menu_mock)
public class TripImagesTabsFragment extends BaseFragment<TripImagesTabsPresenter> implements TripImagesTabsPresenter.View, ViewPager.OnPageChangeListener {

   @InjectView(R.id.tabs) protected BadgedTabLayout tabs;
   @InjectView(R.id.pager) protected ViewPager pager;

   private BaseStatePagerAdapter adapter;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      if (adapter == null) {
         this.adapter = new BaseStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public void setArgs(int position, Fragment fragment) {
               if (fragment instanceof TripImagesListFragment) {
                  TripImagesType type = TripImagesType.values()[position];
                  BaseFragmentWithArgs fragmentWithArgs = (BaseFragmentWithArgs) fragment;
                  fragmentWithArgs.setArgs(new TripsImagesBundle(type, getPresenter().getAccount().getId()));
               }
            }
         };

         this.adapter.add(new FragmentItem(Route.MEMBER_IMAGES, getString(R.string.member_images)));
         this.adapter.add(new FragmentItem(Route.ACCOUNT_IMAGES, getString(R.string.my_images)));
         this.adapter.add(new FragmentItem(Route.THREE_SIXTY_VIDEOS, getString(R.string.three_sixty)));
         this.adapter.add(new FragmentItem(Route.TRIP_LIST_IMAGES, getString(R.string.inspire_me)));
         this.adapter.add(new FragmentItem(Route.TRIP_LIST_IMAGES, getString(R.string.you_should_be_here)));

      }

      this.pager.setAdapter(adapter);
      this.pager.addOnPageChangeListener(this);

      tabs.setupWithPagerBadged(pager);
      TrackingHelper.selectTripImagesTab(TrackingHelper.ACTION_MEMBER_IMAGES);
   }

   @Override
   public void onResume() {
      super.onResume();
      TrackingHelper.viewTripImagesScreen();
   }

   @Override
   public void setSelection(int selection) {
      pager.setCurrentItem(selection, true);
   }


   @Override
   protected TripImagesTabsPresenter createPresenter(Bundle savedInstanceState) {
      return new TripImagesTabsPresenter(getArguments());
   }

   @Override
   public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
   }

   @Override
   public void onPageSelected(int position) {
      String actionTab = "";
      switch (position) {
         case 0:
            actionTab = TrackingHelper.ACTION_MEMBER_IMAGES;
            break;
         case 1:
            actionTab = TrackingHelper.ACTION_MY_IMAGES;
            break;
         case 2:
            actionTab = TrackingHelper.ACTION_360;
            break;
         case 3:
            actionTab = TrackingHelper.ACTION_INSPIRE_ME_IMAGES;
            break;
         case 4:
            actionTab = TrackingHelper.ACTION_YSHB_IMAGES;
            break;
      }
      TrackingHelper.selectTripImagesTab(actionTab);
   }

   @Override
   public void onPageScrollStateChanged(int state) {
   }


}
