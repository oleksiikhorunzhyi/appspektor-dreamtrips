package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.presentation.BucketTabsFragmentPM;
import com.worldventures.dreamtrips.view.adapter.BasePagerAdapter;

import butterknife.InjectView;

import static com.worldventures.dreamtrips.view.fragment.TripImagesListFragment.BUNDLE_TYPE;

@Layout(R.layout.fragment_bucket_tab)
public class BucketTabsFragment extends BaseFragment<BucketTabsFragmentPM> {
    @Override
    protected BucketTabsFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new BucketTabsFragmentPM(this);
    }


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
                    args.putSerializable(BUNDLE_TYPE, type);
                    fragment.setArguments(args);
                }
            };

            this.adapter.add(new BasePagerAdapter.FragmentItem(BucketListFragment.class, "Locations"));
            this.adapter.add(new BasePagerAdapter.FragmentItem(BucketListFragment.class, "Activities"));
            this.adapter.add(new BasePagerAdapter.FragmentItem(BucketListFragment.class, "Restaurants"));

        }
        this.pager.setAdapter(adapter);
        this.tabs.setViewPager(pager);
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
