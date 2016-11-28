package com.worldventures.dreamtrips.modules.infopages.view.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.utils.delegate.ScreenChangedEventDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.CustomViewPager;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.fragment_tab_info)
@MenuResource(R.menu.menu_mock)
public class HelpFragment extends BaseFragment<Presenter> {

   protected static final int TERMS_OFFSCREEN_PAGES = 2;

   @InjectView(R.id.tabs) protected BadgedTabLayout tabs;
   @InjectView(R.id.pager) protected CustomViewPager pager;

   @Inject ScreenChangedEventDelegate screenChangedEventDelegate;

   protected BasePagerAdapter adapter;

   @Override
   protected Presenter createPresenter(Bundle savedInstanceState) {
      return new Presenter();
   }

   @Override
   public void afterCreateView(View rootView) {
      if (adapter == null) {
         this.adapter = new BasePagerAdapter(getChildFragmentManager());
         this.adapter.add(new FragmentItem(Route.HELP_VIDEOS, getString(R.string.presentations)));
         this.adapter.add(new FragmentItem(Route.FAQ, getString(R.string.faq)));
      }

      pager.setAdapter(adapter);
      // Is used for block screen rotates
      pager.setOffscreenPageLimit(TERMS_OFFSCREEN_PAGES);
      pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
         @Override
         public void onPageSelected(int position) {
            super.onPageSelected(position);
            screenChangedEventDelegate.post(null);
         }
      });

      tabs.setupWithPagerBadged(pager);
   }
}
