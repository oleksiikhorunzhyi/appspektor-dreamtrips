package com.worldventures.dreamtrips.view.fragment.reptools;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.presentation.RepToolsFragmentPM;
import com.worldventures.dreamtrips.view.adapter.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.view.adapter.viewpager.FragmentItem;
import com.worldventures.dreamtrips.view.fragment.BaseFragment;

import butterknife.InjectView;

@Layout(R.layout.fragment_rep_tools)
public class RepToolsFragment extends BaseFragment<RepToolsFragmentPM> implements ViewPager.OnPageChangeListener {

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    ViewPager pager;

    BaseStatePagerAdapter adapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        if (adapter == null) {
            this.adapter = new BaseStatePagerAdapter(getChildFragmentManager()) {
                @Override
                public void setArgs(int position, Fragment fragment) {
                    Bundle args = new Bundle();
                    fragment.setArguments(args);
                }
            };

            this.adapter.add(new FragmentItem(SuccessStoresListFragment.class, getString(R.string.success_stores)));

        }
        this.pager.setAdapter(adapter);
        this.tabs.setOnPageChangeListener(this);
        this.tabs.setViewPager(pager);
        this.tabs.setBackgroundColor(getResources().getColor(R.color.theme_main));
    }

    @Override
    protected RepToolsFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new RepToolsFragmentPM(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
