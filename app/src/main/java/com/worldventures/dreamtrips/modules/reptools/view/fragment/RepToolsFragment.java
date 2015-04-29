package com.worldventures.dreamtrips.modules.reptools.view.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.apptentive.android.sdk.Log;
import com.astuetz.PagerSlidingTabStrip;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.reptools.presenter.RepToolsPresenter;

import javax.inject.Inject;

import butterknife.InjectView;


@Layout(R.layout.fragment_rep_tools)
@MenuResource(R.menu.menu_mock)
public class RepToolsFragment extends BaseFragment<RepToolsPresenter> implements ViewPager.OnPageChangeListener {

    @InjectView(R.id.tabs)
    protected PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    protected ViewPager pager;

    private BaseStatePagerAdapter adapter;

    @Inject
    protected FragmentCompass fragmentCompass;

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

            this.adapter.add(new FragmentItem(SuccessStoriesListFragment.class, getString(R.string.success_stories)));
            this.adapter.add(new FragmentItem(StaticInfoFragment.TrainingVideosFragment.class, getString(R.string.training_videos)));
            this.adapter.add(new FragmentItem(StaticInfoFragment.EnrollRepFragment.class, getString(R.string.rep_enrollment)));

        }
        this.pager.setAdapter(adapter);
        this.tabs.setViewPager(pager);
        this.tabs.setBackgroundColor(getResources().getColor(R.color.theme_main));
        pager.setOnPageChangeListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fragmentCompass.pop();
        fragmentCompass.replace(Route.REP_TOOLS);
    }

    @Override
    protected RepToolsPresenter createPresenter(Bundle savedInstanceState) {
        return new RepToolsPresenter(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.v(this.getClass().getSimpleName(), "onPageScrolled");
    }

    @Override
    public void onPageSelected(int position) {
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(pager.getWindowToken(), 0);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.v(this.getClass().getSimpleName(), "onPageScrollStateChanged");
    }
}
