package com.worldventures.dreamtrips.modules.tripsimages.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.common.view.viewpager.SelectablePagerFragment;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.TripsImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;
import com.worldventures.dreamtrips.modules.tripsimages.presenter.TripImagesTabsPresenter;
import com.worldventures.dreamtrips.util.PageSelectionDetector;

import butterknife.InjectView;

@Layout(R.layout.fragment_trip_images_tabs)
@MenuResource(R.menu.menu_mock)
public class TripImagesTabsFragment extends BaseFragment<TripImagesTabsPresenter> implements TripImagesTabsPresenter.View {

   @InjectView(R.id.tabs) protected BadgedTabLayout tabs;
   @InjectView(R.id.pager) protected ViewPager pager;

   private BasePagerAdapter adapter;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      if (adapter == null) {
         this.adapter = new BasePagerAdapter(getChildFragmentManager()) {
            @Override
            public void setArgs(int position, Fragment fragment) {
               //TODO should be refactored
               if (fragment instanceof TripImagesListFragment) {
                  TripImagesType type = TripImagesType.values()[position];
                  BaseFragmentWithArgs fragmentWithArgs = (BaseFragmentWithArgs) fragment;
                  fragmentWithArgs.setArgs(new TripsImagesBundle(type, getPresenter().getAccount().getId()));
               }
            }
         };

         adapter.add(new FragmentItem(Route.MEMBERS_IMAGES, getString(R.string.member_images)));
         adapter.add(new FragmentItem(Route.ACCOUNT_IMAGES, getString(R.string.my_images)));
         adapter.add(new FragmentItem(Route.THREE_SIXTY_VIDEOS, getString(R.string.three_sixty)));
         adapter.add(new FragmentItem(Route.TRIP_LIST_IMAGES, getString(R.string.inspire_me)));
         adapter.add(new FragmentItem(Route.TRIP_LIST_IMAGES, getString(R.string.you_should_be_here)));
      }

      PageSelectionDetector.listenPageSelection(pager, pageNumber -> {
         SelectablePagerFragment fragment = (SelectablePagerFragment) adapter.getCurrentFragment();
         fragment.onSelectedFromPager();
      });

      pager.setAdapter(adapter);
      tabs.setupWithPagerBadged(pager);
   }

   @Override
   public void setSelection(int selection) {
      pager.setCurrentItem(selection, true);
   }

   @Override
   protected TripImagesTabsPresenter createPresenter(Bundle savedInstanceState) {
      return new TripImagesTabsPresenter(getArguments());
   }
}
