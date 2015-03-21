package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketPopularTabsFragmentPM;
import com.worldventures.dreamtrips.modules.bucketlist.view.activity.BucketListEditActivity;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.common.view.viewpager.FragmentItem;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.CustomViewPager;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import butterknife.InjectView;


@Layout(R.layout.fragment_bucket_tab)
public class BucketPopularTabsFragment extends BaseFragment<BucketPopularTabsFragmentPM> implements BucketPopularTabsFragmentPM.View {

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    CustomViewPager pager;
    @InjectView(R.id.v_bg_holder)
    View vBgHolder;
    BasePagerAdapter adapter;

    @Override
    protected BucketPopularTabsFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new BucketPopularTabsFragmentPM(this);
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        BucketTabsFragment.Type type = (BucketTabsFragment.Type) getArguments().getSerializable(BucketListEditActivity.EXTRA_TYPE);

        if (adapter == null) {
            this.adapter = new BasePagerAdapter(getChildFragmentManager()) {
                @Override
                public void setArgs(int position, Fragment fragment) {
                    fragment.setArguments(getPresentationModel().getBundleForPosition(position));
                }
            };

            this.adapter.add(new FragmentItem(BucketListPopuralFragment.class, getString(R.string.bucket_locations)));
            this.adapter.add(new FragmentItem(BucketListPopuralFragment.class, getString(R.string.bucket_activities)));
            //this.adapter.add(new FragmentItem(BucketListPopuralFragment.class, getString(R.string.bucket_restaurants)));
        }

        pager.setAdapter(adapter);
        pager.setPagingEnabled(false);
        tabs.setViewPager(pager);

        pager.setCurrentItem(type.ordinal());
    }

}
