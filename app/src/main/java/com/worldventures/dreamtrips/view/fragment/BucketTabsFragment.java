package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.gc.materialdesign.views.Switch;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.kbeanie.imagechooser.api.ChooserType;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.presentation.BucketTabsFragmentPM;
import com.worldventures.dreamtrips.utils.ViewUtils;
import com.worldventures.dreamtrips.view.adapter.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.view.adapter.viewpager.FragmentItem;
import com.worldventures.dreamtrips.view.custom.CustomViewPager;
import com.worldventures.dreamtrips.view.dialog.PickImageDialog;

import butterknife.InjectView;
import butterknife.OnClick;


@Layout(R.layout.fragment_bucket_tab)
public class BucketTabsFragment extends BaseFragment<BucketTabsFragmentPM> implements BucketTabsFragmentPM.View{

    @Override
    protected BucketTabsFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new BucketTabsFragmentPM(this);
    }

    @InjectView(R.id.sw_liked)
    Switch swLiked;
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

        if (adapter == null) {
            this.adapter = new BasePagerAdapter(getChildFragmentManager()) {
                @Override
                public void setArgs(int position, Fragment fragment) {
                    fragment.setArguments(getPresentationModel().getBundleForPosition(position));
                }
            };

            this.adapter.add(new FragmentItem(BucketListFragment.class, getString(R.string.bucket_locations)));
            this.adapter.add(new FragmentItem(BucketListFragment.class, getString(R.string.bucket_activities)));
            this.adapter.add(new FragmentItem(BucketListFragment.class, getString(R.string.bucket_restaurants)));
        }

        pager.setAdapter(adapter);
        pager.setPagingEnabled(false);
        tabs.setViewPager(pager);
    }

    @Override
    public boolean isTabletLandscape() {
        return ViewUtils.isTablet(getActivity()) && ViewUtils.isLandscapeOrientation(getActivity());
    }

    public enum Type {
        LOCATIONS("location", R.string.location),
        ACTIVITIES("activity", R.string.activity),
        RESTAURANTS("dinning", R.string.dinning);

        Type(String name, int res) {
            this.name = name;
            this.res = res;
        }

        String name;
        int res;

        public String getName() {
            return name;
        }
    }
}
