package com.worldventures.dreamtrips.social.ui.reptools.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.annotations.MenuResource;
import com.worldventures.core.ui.util.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.social.ui.reptools.presenter.RepToolsPresenter;
import com.worldventures.dreamtrips.social.util.event_delegate.ScreenChangedEventDelegate;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.fragment_rep_tools)
@MenuResource(R.menu.menu_mock)
public class RepToolsFragment extends BaseFragment<RepToolsPresenter> implements ViewPager.OnPageChangeListener, RepToolsPresenter.View {

   @InjectView(R.id.tabs) protected BadgedTabLayout tabs;
   @InjectView(R.id.pager) protected ViewPager pager;

   private BaseStatePagerAdapter adapter;

   @Inject ScreenChangedEventDelegate screenChangedEventDelegate;

   @Override
   protected RepToolsPresenter createPresenter(Bundle savedInstanceState) {
      return new RepToolsPresenter();
   }

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);
      adapter = new BaseStatePagerAdapter(getChildFragmentManager()) {
         @Override
         public void setArgs(int position, Fragment fragment) {
            Bundle args = new Bundle();
            fragment.setArguments(args);
         }
      };
      pager.setAdapter(adapter);
      pager.addOnPageChangeListener(this);
   }

   @Override
   public void setScreens(List<? extends FragmentItem> items) {
      adapter.addItems(items);
      adapter.notifyDataSetChanged();
      tabs.setupWithPagerBadged(pager);
   }

   @Override
   public void toggleTabStripVisibility(boolean isVisible) {
      tabs.setVisibility(isVisible ? View.VISIBLE : View.GONE);
   }

   @Override
   public void onPageSelected(int position) {
      getPresenter().trackState(position);
      SoftInputUtil.hideSoftInputMethod(pager);
      screenChangedEventDelegate.post(null);
   }

   @Override
   public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
   }

   @Override
   public void onPageScrollStateChanged(int state) {
   }
}
