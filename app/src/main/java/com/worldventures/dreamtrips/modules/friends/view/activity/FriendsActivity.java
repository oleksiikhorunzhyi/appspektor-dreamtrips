package com.worldventures.dreamtrips.modules.friends.view.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendsMainPresenter;
import com.worldventures.dreamtrips.modules.friends.view.fragment.FriendListFragment;
import com.worldventures.dreamtrips.modules.friends.view.fragment.RequestsFragment;

import butterknife.InjectView;

@Layout(R.layout.activity_friends)
public class FriendsActivity extends ActivityWithPresenter<FriendsMainPresenter> implements FriendsMainPresenter.View {

    @InjectView(R.id.tabs)
    BadgedTabLayout tabLayout;
    @InjectView(R.id.viewpager)
    ViewPager pager;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    BasePagerAdapter<FragmentItem> adapter;

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ab.setDisplayHomeAsUpEnabled(true);

        adapter = new BasePagerAdapter<>(getSupportFragmentManager());
        adapter.add(new FragmentItem(FriendListFragment.class, getString(R.string.social_my_friends)));
        adapter.add(new FragmentItem(RequestsFragment.class, getString(R.string.social_requests)));

        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (pager.getCurrentItem() == 1) {
                    updateItems(0);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithPagerBadged(pager);
    }

    @Override
    public void setRecentItems(int count) {
        if (pager.getCurrentItem() == 0) {
            updateItems(count);
        }
    }

    private void updateItems(int count) {
        tabLayout.setBadgeCount(1, count);
    }

    @Override
    protected FriendsMainPresenter createPresentationModel(Bundle savedInstanceState) {
        return new FriendsMainPresenter();
    }

}
