package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.gc.materialdesign.views.Switch;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.CustomViewPager;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;

import butterknife.InjectView;


@Layout(R.layout.fragment_bucket_tab)
public class BucketTabsFragment extends BaseFragment<BucketTabsPresenter> implements BucketTabsPresenter.View {

    @InjectView(R.id.tabs)
    protected PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    protected CustomViewPager pager;

    private BasePagerAdapter adapter;

    @Override
    protected BucketTabsPresenter createPresenter(Bundle savedInstanceState) {
        return new BucketTabsPresenter(this);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        if (adapter == null) {
            this.adapter = new BasePagerAdapter(getChildFragmentManager()) {
                @Override
                public void setArgs(int position, Fragment fragment) {
                    fragment.setArguments(getPresenter().getBundleForPosition(position));
                }
            };

            this.adapter.add(new FragmentItem(BucketListFragment.class, getString(R.string.bucket_locations)));
            this.adapter.add(new FragmentItem(BucketListFragment.class, getString(R.string.bucket_activities)));
        }

        pager.setAdapter(adapter);
        pager.setPagingEnabled(false);
        tabs.setViewPager(pager);
    }

    public enum Type {
        LOCATIONS("location", R.string.location),
        ACTIVITIES("activity", R.string.activity),
        RESTAURANTS("dinning", R.string.dinning);
        protected String name;
        protected int res;

        Type(String name, int res) {
            this.name = name;
            this.res = res;
        }

        public String getName() {
            return name;
        }
    }
}
