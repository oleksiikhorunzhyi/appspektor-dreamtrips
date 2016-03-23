package com.worldventures.dreamtrips.modules.membership.view.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.utils.event.ScreenChangedEvent;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.membership.presenter.MembershipPresenter;
import com.worldventures.dreamtrips.modules.video.view.PresentationVideosFragment;

import butterknife.InjectView;

@Layout(R.layout.fragment_membership)
@MenuResource(R.menu.menu_mock)
public class MembershipFragment extends BaseFragment<MembershipPresenter> implements MembershipPresenter.View, ViewPager.OnPageChangeListener {

    @InjectView(R.id.tabs)
    BadgedTabLayout tabs;
    @InjectView(R.id.pager)
    ViewPager pager;

    BaseStatePagerAdapter adapter;

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

        adapter.add(new FragmentItem(Route.PRESENTATION_VIDEOS, getString(R.string.presentations)));
        adapter.add(new FragmentItem(Route.ENROLL, getString(R.string.enroll_member)));
        if (getPresenter().showInvite()) {
            adapter.add(new FragmentItem(Route.INVITE, getString(R.string.invite_and_share)));
        }
        adapter.notifyDataSetChanged();

        tabs.setupWithPagerBadged(pager);
        TrackingHelper.viewMembershipScreen(TrackingHelper.ACTION_MEMBERSHIP);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        getPresenter().trackState(position);
        SoftInputUtil.hideSoftInputMethod(pager);
        eventBus.post(new ScreenChangedEvent());
        TrackingHelper.viewMembershipScreen(position == 0 ? TrackingHelper.ACTION_MEMBERSHIP : TrackingHelper.ACTION_MEMBERSHIP_ENROLL);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
