package com.worldventures.dreamtrips.modules.membership.view.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.membership.presenter.MembershipPresenter;

import butterknife.InjectView;

@Layout(R.layout.fragment_member_ship)
public class MembershipFragment extends BaseFragment<MembershipPresenter> {

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabStrip;
    @InjectView(R.id.pager)
    ViewPager pager;

    BaseStatePagerAdapter adapter;

    @Override
    protected MembershipPresenter createPresenter(Bundle savedInstanceState) {
        return new MembershipPresenter(this);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        adapter = new BaseStatePagerAdapter(getChildFragmentManager());
        pager.setAdapter(adapter);
        tabStrip.setViewPager(pager);

        adapter.add(new FragmentItem(PresentationsFragment.class, getString(R.string.presentations)));
        adapter.add(new FragmentItem(StaticInfoFragment.EnrollFragment.class, getString(R.string.enroll_member)));
        adapter.add(new FragmentItem(InviteFragment.class, getString(R.string.invite_and_share)));
        adapter.notifyDataSetChanged();

    }
}