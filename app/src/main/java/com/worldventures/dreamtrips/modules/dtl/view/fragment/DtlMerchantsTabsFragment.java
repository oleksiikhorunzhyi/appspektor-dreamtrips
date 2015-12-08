package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantDetailsBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantsBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.MerchantsMapBundle;
import com.worldventures.dreamtrips.modules.dtl.event.DtlSearchMerchantRequestEvent;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlMerchantSearchViewDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMerchantsTabsPresenter;

import java.util.List;

import butterknife.InjectView;
import icepick.State;

@Layout(R.layout.fragment_dtl_merchants_tabs)
public class DtlMerchantsTabsFragment
        extends BaseFragmentWithArgs<DtlMerchantsTabsPresenter, MerchantsBundle>
        implements DtlMerchantsTabsPresenter.View {

    @InjectView(R.id.tabs)
    BadgedTabLayout tabStrip;
    @InjectView(R.id.pager)
    CustomViewPager pager;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    BasePagerAdapter<DataFragmentItem> adapter;
    @State
    int currentPosition;
    @State
    String lastQuery;

    @Override
    protected DtlMerchantsTabsPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlMerchantsTabsPresenter(getArgs().getLocation());
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
                getPresenter().trackTabChange(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        toolbar.inflateMenu(R.menu.menu_dtl_list);
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        new DtlMerchantSearchViewDelegate(getContext()).init(searchItem, lastQuery, query -> {
            lastQuery = query;
            eventBus.post(new DtlSearchMerchantRequestEvent(query));
        });

        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_map:
                    router.moveTo(Route.DTL_MAP, NavigationConfigBuilder.forFragment().useDefaults()
                            .data(new MerchantsMapBundle(getArgs().getLocation(), false))
                            .fragmentManager(getParentFragment().getFragmentManager())
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
    public void setTypes(List<DtlMerchantType> types) {
        if (adapter.getCount() == 0) {
            for (DtlMerchantType type : types) {
                adapter.add(new DataFragmentItem<>(DtlMerchantsListFragment.class, getString(type.getCaptionResId()), type));
            }
            adapter.notifyDataSetChanged();
        }
        tabStrip.setupWithPagerBadged(pager);
        //
        tabStrip.getTabAt(1).select();
    }

    @Override
    public void updateSelection() {
        pager.setCurrentItem(currentPosition);
    }

    @Override
    public void initToolbar(DtlLocation location) {
        if (!tabletAnalytic.isTabletLandscape()) {
            toolbar.setNavigationIcon(R.drawable.ic_menu_hamburger);
        }
        toolbar.setNavigationOnClickListener(view -> ((MainActivity) getActivity()).openLeftDrawer());
        View title = toolbar.findViewById(R.id.spinnerStyledTitle);
        if (title != null) {
            title.setOnClickListener(v ->
                    router.moveTo(Route.DTL_LOCATIONS, NavigationConfigBuilder.forFragment()
                            .backStackEnabled(false)
                            .containerId(R.id.dtl_container)
                            .fragmentManager(getParentFragment().getFragmentManager())
                            .build()));
            ((TextView) title).setText(location.getLongName());
        }
    }

    @Override
    public void openDetails(DtlMerchant merchant) {
        router.moveTo(Route.DTL_MERCHANT_DETAILS, NavigationConfigBuilder.forActivity()
                .data(new MerchantDetailsBundle(merchant, false))
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .build());
    }
}