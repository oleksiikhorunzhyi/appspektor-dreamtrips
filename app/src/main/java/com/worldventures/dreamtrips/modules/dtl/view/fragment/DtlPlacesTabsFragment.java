package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.CustomViewPager;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.adapter.item.DataFragmentItem;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragmentWithArgs;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlaceDetailsBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlacesTabsPresenter;

import java.util.List;

import butterknife.InjectView;
import icepick.State;

@Layout(R.layout.fragment_dtl_places_tabs)
public class DtlPlacesTabsFragment
        extends BaseFragmentWithArgs<DtlPlacesTabsPresenter, PlacesBundle>
        implements DtlPlacesTabsPresenter.View {

    @InjectView(R.id.tabs)
    BadgedTabLayout tabStrip;
    @InjectView(R.id.pager)
    CustomViewPager pager;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    BasePagerAdapter<DataFragmentItem> adapter;
    @State
    int currentPosition;

    @Override
    protected DtlPlacesTabsPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlPlacesTabsPresenter(getArgs().getLocation());
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        if (adapter == null) {
            adapter = new BasePagerAdapter<DataFragmentItem>(getChildFragmentManager()) {
                @Override
                public void setArgs(int position, Fragment fragment) {
                    fragment.setArguments(getPresenter().prepareArgsForTab(position));
                }
            };
        }
        pager.setAdapter(adapter);
        pager.setPagingEnabled(false);
        pager.setOffscreenPageLimit(1);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        toolbar.inflateMenu(R.menu.menu_dtl_list);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_map:
                    router.moveTo(Route.DTL_MAP, NavigationConfigBuilder.forFragment().useDefaults()
                            .data(getArgs())
                            .fragmentManager(getFragmentManager())
                            .containerId(R.id.dtl_container)
                            .build());
                    break;
                case R.id.action_dtl_filter:
                    ((MainActivity) getActivity()).openRightDrawer();
                    break;
            }
            return super.onOptionsItemSelected(item);
        });
    }

    @Override
    public void setTypes(List<DtlPlaceType> types) {
        if (adapter.getCount() == 0) {
            for (DtlPlaceType type : types) {
                adapter.add(new DataFragmentItem<>(DtlPlacesListFragment.class, getString(type.getCaptionResId()), type));
            }
            adapter.notifyDataSetChanged();
        }
        tabStrip.setupWithPagerBadged(pager);
    }

    @Override
    public void updateSelection() {
        pager.setCurrentItem(currentPosition);
    }

    @Override
    public void initToolbar(DtlLocation location) {
        FragmentManager fragmentManager;
        if (!tabletAnalytic.isTabletLandscape()) {
            toolbar.setNavigationIcon(R.drawable.ic_menu_hamburger);
            fragmentManager = getFragmentManager();
        } else {
            fragmentManager = getParentFragment().getFragmentManager();
        }
        toolbar.setNavigationOnClickListener(view -> ((MainActivity) getActivity()).openLeftDrawer());
        toolbar.findViewById(R.id.spinnerStyledTitle).setOnClickListener(v ->
                router.moveTo(Route.DTL_LOCATIONS, NavigationConfigBuilder.forFragment()
                        .backStackEnabled(false)
                        .containerId(R.id.dtl_container)
                        .fragmentManager(fragmentManager)
                        .build()));
        ((TextView) toolbar.findViewById(R.id.spinnerStyledTitle)).setText(location.getLongName());
    }

    @Override
    public void openDetails(DtlPlace place) {
        router.moveTo(Route.DTL_PLACE_DETAILS, NavigationConfigBuilder.forActivity()
                .data(new PlaceDetailsBundle(place, false))
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .build());
    }
}
