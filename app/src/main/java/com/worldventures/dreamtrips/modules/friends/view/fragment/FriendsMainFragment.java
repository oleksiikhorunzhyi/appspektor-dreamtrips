package com.worldventures.dreamtrips.modules.friends.view.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.friends.bundle.FriendMainBundle;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendsMainPresenter;

import butterknife.InjectView;

@Layout(R.layout.fragment_friends_base)
public class FriendsMainFragment extends BaseFragmentWithArgs<FriendsMainPresenter, FriendMainBundle> implements FriendsMainPresenter.View {

   @InjectView(R.id.tabs) BadgedTabLayout tabLayout;
   @InjectView(R.id.viewpager) ViewPager pager;

   BasePagerAdapter<FragmentItem> adapter;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);

      adapter = new BasePagerAdapter<>(getChildFragmentManager());
      adapter.add(new FragmentItem(Route.FRIEND_LIST, getString(R.string.social_my_friends)));
      adapter.add(new FragmentItem(Route.FRIEND_REQUESTS, getString(R.string.social_requests)));

      pager.setAdapter(adapter);
      pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         @Override
         public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

         }

         @Override
         public void onPageSelected(int position) {
            if (pager.getCurrentItem() == 1) {
               updateItems(0);
            }
         }

         @Override
         public void onPageScrollStateChanged(int state) {

         }
      });

      tabLayout.setupWithPagerBadged(pager);
      tabLayout.getTabAt(pager.getCurrentItem()).select();


      if (getArgs() != null) {
         pager.setCurrentItem(getArgs().getDefaultPosition());
      }
   }

   @Override
   public void setRecentItems(int count) {
      if (pager.getCurrentItem() == 0) {
         updateItems(count);
      }
   }

   private void updateItems(int count) {
      tabLayout.setBadgeCount(1, count);
   }

   @Override
   protected FriendsMainPresenter createPresenter(Bundle savedInstanceState) {
      return new FriendsMainPresenter();
   }

}
