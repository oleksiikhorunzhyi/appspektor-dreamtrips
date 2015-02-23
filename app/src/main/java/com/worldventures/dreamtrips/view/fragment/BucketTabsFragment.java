package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.gc.materialdesign.views.Switch;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.presentation.BucketTabsFragmentPM;
import com.worldventures.dreamtrips.view.adapter.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.view.adapter.viewpager.FragmentItem;

import butterknife.InjectView;


@Layout(R.layout.fragment_bucket_tab)
public class BucketTabsFragment extends BaseFragment<BucketTabsFragmentPM> {


    @Override
    protected BucketTabsFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new BucketTabsFragmentPM(this);
    }

    @InjectView(R.id.sw_liked)
    Switch swLiked;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @InjectView(R.id.pager)
    ViewPager pager;

    BasePagerAdapter adapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        if (adapter == null) {
            this.adapter = new BasePagerAdapter(getChildFragmentManager()) {
                @Override
                public void setArgs(int position, Fragment fragment) {
                    Bundle args = new Bundle();
                    Type type = Type.values()[position];
                    args.putSerializable(BucketListFragment.BUNDLE_TYPE, type);
                    fragment.setArguments(args);
                }
            };

            this.adapter.add(new FragmentItem(BucketListFragment.class, getString(R.string.bucket_locations)));
            this.adapter.add(new FragmentItem(BucketListFragment.class, getString(R.string.bucket_activities)));
            this.adapter.add(new FragmentItem(BucketListFragment.class, getString(R.string.bucket_restaurants)));
        }

        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        swLiked.setChecked(getPresentationModel().isFilterEnabled());
        swLiked.setOncheckListener(b -> {
            getPresentationModel().filterEnabled(b);
            for (int i = 0; i < adapter.getCount(); i++) {
                String id = "android:switcher:" + R.id.pager + ":" + i;
                Fragment page = getChildFragmentManager().findFragmentByTag(id);
                if (page != null) {
                    ((BucketListFragment) page).requestReload();
                }
            }
        });
    }

    public enum Type {
        LOCATIONS("bucket/bucket_list_locations.json"),
        ACTIVITIES("bucket/bucket_list_activities.json"),
        RESTAURANTS("bucket/bucket_list_restaurants.json");

        Type(String fileName) {
            this.fileName = fileName;
        }

        String fileName;

        public String getFileName() {
            return fileName;
        }
    }
}
