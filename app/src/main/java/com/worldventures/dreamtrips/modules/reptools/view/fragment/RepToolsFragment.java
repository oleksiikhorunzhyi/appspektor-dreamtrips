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
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BaseStatePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.membership.view.fragment.InviteFragment;
import com.worldventures.dreamtrips.modules.reptools.presenter.RepToolsPresenter;

import javax.inject.Inject;

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

    @Inject
    protected FragmentCompass fragmentCompass;

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

            adapter.add(new FragmentItem(TrainingVideosFragment.class, getString(R.string.training_videos)));
            adapter.add(new FragmentItem(StaticInfoFragment.EnrollRepFragment.class, getString(R.string.rep_enrollment)));
            adapter.add(new FragmentItem(SuccessStoryListFragment.class, getString(R.string.success_stories)));

            if (getPresenter().showInvite()) {
                adapter.add(new FragmentItem(InviteFragment.class, getString(R.string.invite_and_share)));
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
        getPresenter().trackState(position);
        SoftInputUtil.hideSoftInputMethod(pager);
        eventBus.post(new ScreenChangedEvent());

        Fragment tab = adapter.getItem(position);
        if (tab instanceof InviteFragment) {
            TrackingHelper.actionRepToolsInviteShare(TrackingHelper.ATTRIBUTE_VIEW);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
