package com.worldventures.dreamtrips.modules.reptools.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.utils.event.ScreenChangedEvent;
import com.techery.spares.utils.ui.SoftInputUtil;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.reptools.presenter.RepToolsPresenter;

import butterknife.InjectView;

@Layout(R.layout.fragment_rep_tools)
@MenuResource(R.menu.menu_mock)
public class RepToolsFragment extends BaseFragment<RepToolsPresenter> implements
        ViewPager.OnPageChangeListener, RepToolsPresenter.View {

    @InjectView(R.id.tabs)
    protected BadgedTabLayout tabs;
    @InjectView(R.id.pager)
    protected ViewPager pager;

    private BaseStatePagerAdapter adapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        if (adapter == null) {
            adapter = new BaseStatePagerAdapter(getChildFragmentManager()) {
                @Override
                public void setArgs(int position, Fragment fragment) {
                    Bundle args = new Bundle();
                    fragment.setArguments(args);
                }
            };

            adapter.add(new FragmentItem(Route.TRAINING_VIDEOS, getString(R.string.training_videos)));
            adapter.add(new FragmentItem(Route.ENROLL_REP, getString(R.string.rep_enrollment)));

            if (getPresenter().showSuggestMerchant())
                adapter.add(new FragmentItem(Route.SUGGEST_RESTAURANT, getString(R.string.rep_suggest_restaurant)));

            adapter.add(new FragmentItem(Route.SUCCESS_STORY_LIST, getString(R.string.success_stories)));

            if (getPresenter().showInvite()) {
                adapter.add(new FragmentItem(Route.INVITE, getString(R.string.invite_and_share)));
            }
        }
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(this);

        tabs.setupWithPagerBadged(pager);
    }

    @Override
    public void toggleTabStripVisibility(boolean isVisible) {
        tabs.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    protected RepToolsPresenter createPresenter(Bundle savedInstanceState) {
        return new RepToolsPresenter();
    }

    @Override
    public void onPageSelected(int position) {
        getPresenter().trackState(adapter.getItem(position).getClass());
        SoftInputUtil.hideSoftInputMethod(pager);
        eventBus.post(new ScreenChangedEvent());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
