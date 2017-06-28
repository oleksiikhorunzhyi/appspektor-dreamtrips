package com.worldventures.dreamtrips.modules.infopages.view.fragment;

import android.os.Bundle;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.CustomViewPager;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.infopages.presenter.HelpTabPresenter;

import butterknife.InjectView;

@Layout(R.layout.fragment_tab_info)
@MenuResource(R.menu.menu_mock)
public class HelpFragment extends BaseFragment<HelpTabPresenter> {

   protected static final int TERMS_OFFSCREEN_PAGES = 2;

   @InjectView(R.id.tabs) protected BadgedTabLayout tabs;
   @InjectView(R.id.pager) protected CustomViewPager pager;

   protected BasePagerAdapter adapter;

   @Override
   protected HelpTabPresenter createPresenter(Bundle savedInstanceState) {
      return new HelpTabPresenter();
   }

   @Override
   public void afterCreateView(View rootView) {
      if (adapter == null) {
         this.adapter = new BasePagerAdapter(getChildFragmentManager());
         this.adapter.add(new FragmentItem(Route.HELP_DOCUMENTS_LIST, getString(R.string.documents)));
         this.adapter.add(new FragmentItem(Route.HELP_VIDEOS, getString(R.string.presentations)));
      }

      pager.setAdapter(adapter);
      // Is used for block screen rotates
      pager.setOffscreenPageLimit(TERMS_OFFSCREEN_PAGES);
      tabs.setupWithPagerBadged(pager);
   }
}
