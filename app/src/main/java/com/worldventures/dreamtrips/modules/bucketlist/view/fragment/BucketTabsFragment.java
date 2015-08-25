package com.worldventures.dreamtrips.modules.bucketlist.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.CustomViewPager;
import com.worldventures.dreamtrips.modules.common.view.adapter.item.DataFragmentItem;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import icepick.Icicle;

import static com.worldventures.dreamtrips.modules.bucketlist.presenter.BucketTabsPresenter.BucketType;


@Layout(R.layout.fragment_bucket_tab)
@MenuResource(R.menu.menu_mock)
public class BucketTabsFragment<PRESENTER extends BucketTabsPresenter> extends BaseFragment<PRESENTER> implements BucketTabsPresenter.View {

    @InjectView(R.id.tabs)
    BadgedTabLayout tabStrip;
    @InjectView(R.id.pager)
    CustomViewPager pager;
    BasePagerAdapter<DataFragmentItem> adapter;

    @Icicle
    int currentPosition;

    @Override
    protected PRESENTER createPresenter(Bundle savedInstanceState) {
        return (PRESENTER) new BucketTabsPresenter();
    }

    @NonNull
    protected Bundle createListFragmentArgs(int position) {
        Bundle args = new Bundle();
        Serializable type = adapter.getFragmentItem(position).data;
        args.putSerializable(BucketListFragment.BUNDLE_TYPE, type);
        args.putBoolean(BucketListFragment.BUNDLE_DRAG_ENABLED, true);
        return args;
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        if (adapter == null) {
            adapter = new BasePagerAdapter<DataFragmentItem>(getChildFragmentManager()) {
                @Override
                public void setArgs(int position, Fragment fragment) {
                    super.setArgs(position, fragment);
                    Bundle args = createListFragmentArgs(position);
                    fragment.setArguments(args);
                }

            };
        }

        pager.setAdapter(adapter);
        pager.setPagingEnabled(false);
        pager.setOffscreenPageLimit(2);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                notifyPosition();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void openDetails(Bundle args) {
        Route detailsRoute = getDetailsRoute();
        if (isTabletLandscape()) {
            fragmentCompass.disableBackStack();
            fragmentCompass.setSupportFragmentManager(getChildFragmentManager());
            fragmentCompass.setContainerId(R.id.detail_container);
            NavigationBuilder.create()
                    .with(fragmentCompass)
                    .args(args)
                    .move(detailsRoute);
        } else {
            NavigationBuilder.create()
                    .with(activityRouter)
                    .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                    .args(args)
                    .move(detailsRoute);
        }
    }


    protected Route getDetailsRoute() {
        return Route.DETAIL_BUCKET;
    }


    @Override
    public void onResume() {
        super.onResume();
        notifyPosition();
    }

    private void notifyPosition() {
        getPresenter().onTabChange(BucketType.values()[currentPosition]);
    }

    @Override
    public void updateSelection() {
        pager.setCurrentItem(currentPosition);
    }

    @Override
    public void setTypes(List<BucketType> types) {
        if (adapter.getCount() == 0) {
            for (BucketType type : types) {
                adapter.add(new DataFragmentItem<>(BucketListFragment.class, getString(type.getRes()), type));
            }
            adapter.notifyDataSetChanged();
        }
        //
        tabStrip.setupWithPagerBadged(pager);
    }

    @Override
    public void setRecentBucketItemsCount(Map<BucketType, Integer> items) {
        for (BucketType type : items.keySet()) {
            tabStrip.setBadgeCount(type.ordinal(), items.get(type));
        }
    }

    @Override
    public void resetRecentlyAddedBucketItem(BucketType type) {
        tabStrip.setBadgeCount(type.ordinal(), 0);
    }

}
