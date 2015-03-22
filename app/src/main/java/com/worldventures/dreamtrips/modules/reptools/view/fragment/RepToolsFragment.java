package com.worldventures.dreamtrips.modules.reptools.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.reptools.presenter.RepToolsPresenter;

import butterknife.InjectView;

@Layout(R.layout.fragment_rep_tools)
public class RepToolsFragment extends BaseFragment<RepToolsPresenter> implements ViewPager.OnPageChangeListener {

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

            this.adapter.add(new FragmentItem(StaticInfoFragment.TrainingVideosFragment.class, getString(R.string.training_videos)));
            this.adapter.add(new FragmentItem(SuccessStoriesListFragment.class, getString(R.string.success_stories)));

        }
        this.pager.setAdapter(adapter);
        this.tabs.setOnPageChangeListener(this);
        this.tabs.setViewPager(pager);
        this.tabs.setBackgroundColor(getResources().getColor(R.color.theme_main));
    }

    @Override
    protected RepToolsPresenter createPresenter(Bundle savedInstanceState) {
        return new RepToolsPresenter(this);
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
