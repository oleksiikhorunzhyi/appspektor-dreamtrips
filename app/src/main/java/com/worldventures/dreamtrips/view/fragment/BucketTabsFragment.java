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
import com.worldventures.dreamtrips.view.adapter.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.view.adapter.viewpager.FragmentItem;
import com.worldventures.dreamtrips.view.custom.CustomViewPager;
import com.worldventures.dreamtrips.view.dialog.PickImageDialog;

import butterknife.InjectView;
import butterknife.OnClick;


@Layout(R.layout.fragment_bucket_tab)
public class BucketTabsFragment extends BaseFragment<BucketTabsFragmentPM>  implements FloatingActionsMenu.OnFloatingActionsMenuUpdateListener{

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
    @InjectView(R.id.multiple_actions_down)
    FloatingActionsMenu multipleActionsDown;

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
        this.multipleActionsDown.setOnFloatingActionsMenuUpdateListener(this);

    }

    @Override
    public void onMenuExpanded() {
        this.vBgHolder.setBackgroundColor(getResources().getColor(R.color.black_semi_transparent));
    }

    @Override
    public void onMenuCollapsed() {
        this.vBgHolder.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    @OnClick(R.id.fab_own)
    public void actionGallery(View view) {
        getPresentationModel().addOwn(pager.getCurrentItem());
        this.multipleActionsDown.collapse();
    }


    @OnClick(R.id.fab_popular)
    public void actionPopular(View view) {
        getPresentationModel().addPopular(pager.getCurrentItem());
        this.multipleActionsDown.collapse();
    }

    public enum Type {
        LOCATIONS("bucket/bucket_list_locations.json", R.string.location),
        ACTIVITIES("bucket/bucket_list_activities.json", R.string.activity),
        RESTAURANTS("bucket/bucket_list_restaurants.json", R.string.dinning);

        Type(String fileName, int res) {
            this.fileName = fileName;
            this.res = res;
        }

        String fileName;
        int res;

        public String getFileName() {
            return fileName;
        }
    }
}
