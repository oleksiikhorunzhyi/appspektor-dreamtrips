package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.gc.materialdesign.views.Switch;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.presentation.BucketPopularTabsFragmentPM;
import com.worldventures.dreamtrips.presentation.BucketTabsFragmentPM;
import com.worldventures.dreamtrips.utils.ViewUtils;
import com.worldventures.dreamtrips.view.activity.BucketListEditActivity;
import com.worldventures.dreamtrips.view.adapter.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.view.adapter.viewpager.FragmentItem;
import com.worldventures.dreamtrips.view.custom.CustomViewPager;

import butterknife.InjectView;
import butterknife.OnClick;


@Layout(R.layout.fragment_bucket_tab)
public class BucketPopularTabsFragment extends BaseFragment<BucketPopularTabsFragmentPM> implements BucketPopularTabsFragmentPM.View {

    @Override
    protected BucketPopularTabsFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new BucketPopularTabsFragmentPM(this);
    }

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;

    @InjectView(R.id.pager)
    CustomViewPager pager;

    @InjectView(R.id.v_bg_holder)
    View vBgHolder;

    BasePagerAdapter adapter;

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
