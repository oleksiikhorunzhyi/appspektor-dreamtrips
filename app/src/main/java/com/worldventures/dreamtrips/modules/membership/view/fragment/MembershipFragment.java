package com.worldventures.dreamtrips.modules.membership.view.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.utils.delegate.ScreenChangedEventDelegate;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.membership.presenter.MembershipPresenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;

@Layout(R.layout.fragment_membership)
@MenuResource(R.menu.menu_mock)
public class MembershipFragment extends BaseFragment<MembershipPresenter> implements MembershipPresenter.View, ViewPager.OnPageChangeListener {

    @InjectView(R.id.tabs)
    BadgedTabLayout tabs;
    @InjectView(R.id.pager)
    ViewPager pager;

    BaseStatePagerAdapter adapter;
    @Inject ScreenChangedEventDelegate screenChangedEventDelegate;

    @Override
    protected MembershipPresenter createPresenter(Bundle savedInstanceState) {
        return new MembershipPresenter();
    }

    @Override
    public void toggleTabStripVisibility(boolean isVisible) {
        tabs.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        adapter = new BaseStatePagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(this);
    }

    public void setScreens(List<FragmentItem> items) {
        adapter.addItems(items);
        adapter.notifyDataSetChanged();
        tabs.setupWithPagerBadged(pager);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        SoftInputUtil.hideSoftInputMethod(pager);
        screenChangedEventDelegate.post(null);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
