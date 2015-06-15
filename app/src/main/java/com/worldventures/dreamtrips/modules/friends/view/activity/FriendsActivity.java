package com.worldventures.dreamtrips.modules.friends.view.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.activity.ActivityWithPresenter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.friends.presenter.FriendsMainPresenter;
import com.worldventures.dreamtrips.modules.friends.view.fragment.AccountFriendsFragment;

import butterknife.InjectView;

@Layout(R.layout.activity_friends)
public class FriendsActivity extends ActivityWithPresenter<FriendsMainPresenter> {

    @InjectView(R.id.tabs)
    TabLayout tabLayout;
    @InjectView(R.id.viewpager)
    ViewPager pager;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    BucketTabsAdapter adapter;

    @Override
    protected void afterCreateView(Bundle savedInstanceState) {
        super.afterCreateView(savedInstanceState);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        adapter = new BucketTabsAdapter(getSupportFragmentManager());
        adapter.add(new FragmentItem(AccountFriendsFragment.class, getString(R.string.social_my_friends)));
        adapter.add(new FragmentItem(AccountFriendsFragment.class, getString(R.string.social_my_friends)));
        adapter.add(new FragmentItem(AccountFriendsFragment.class, getString(R.string.social_my_friends)));
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);


    }

    @Override
    protected FriendsMainPresenter createPresentationModel(Bundle savedInstanceState) {
        return new FriendsMainPresenter();
    }


    public static class BucketTabsAdapter extends BasePagerAdapter<FragmentItem> {
        public BucketTabsAdapter(FragmentManager fm) {
            super(fm);
        }
    }
}
