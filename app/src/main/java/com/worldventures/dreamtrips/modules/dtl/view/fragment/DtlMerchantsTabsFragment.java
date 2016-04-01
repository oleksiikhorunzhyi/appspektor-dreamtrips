package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.RxLifecycle;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.core.rx.viewbinding.DtRxBindings;
import com.worldventures.dreamtrips.modules.bucketlist.view.custom.CustomViewPager;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.adapter.item.DataFragmentItem;
import com.worldventures.dreamtrips.modules.common.view.custom.BadgedTabLayout;
import com.worldventures.dreamtrips.modules.common.view.viewpager.BasePagerAdapter;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlLocationsBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlMapBundle;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlMerchantDetailsBundle;
import com.worldventures.dreamtrips.modules.dtl.helper.SearchViewHelper;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlMerchantsTabsPresenter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import icepick.State;
import rx.android.schedulers.AndroidSchedulers;

@Layout(R.layout.fragment_dtl_merchants_tabs)
public class DtlMerchantsTabsFragment extends RxBaseFragment<DtlMerchantsTabsPresenter>
        implements DtlMerchantsTabsPresenter.View {

    @InjectView(R.id.tabs)
    BadgedTabLayout tabStrip;
    @InjectView(R.id.pager)
    CustomViewPager pager;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    //
    SearchViewHelper searchViewHelper;
    BasePagerAdapter<DataFragmentItem> adapter;
    //
    @State
    int currentPosition;
    @State
    String lastQuery;

    @Override
    protected DtlMerchantsTabsPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlMerchantsTabsPresenter();
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
        //
        toolbar.inflateMenu(R.menu.menu_dtl_list);
        searchViewHelper = new SearchViewHelper();
        searchViewHelper.init(toolbar.getMenu().findItem(R.id.action_search), lastQuery,
                query -> {
                    lastQuery = query;
                    getPresenter().applySearch(query);
                }, () -> getPresenter().trackTabChange(currentPosition));
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_map:
                    router.moveTo(Route.DTL_MAP, NavigationConfigBuilder.forFragment().useDefaults()
                            .data(new DtlMapBundle(false))
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
        //
        initToolbar();
    }

    @Override
    public void setTabChangeListener() {
        DtRxBindings.observePageSelections(pager)
                .compose(RxLifecycle.bindUntilFragmentEvent(lifecycle(), FragmentEvent.PAUSE))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(position -> currentPosition = position)
                .doOnNext(position -> getPresenter().trackTabChange(currentPosition))
                .subscribe(getPresenter()::rememberUserTabSelection);
    }

    @Override
    public void setTypes(List<DtlMerchantType> types) {
        if (adapter.getCount() == 0) {
            for (DtlMerchantType type : types) {
                adapter.add(new DataFragmentItem<>(Route.DTL_MERCHANTS_LIST, getString(type.getCaptionResId()), type));
            }
            adapter.notifyDataSetChanged();
        }
        tabStrip.setupWithPagerBadged(pager);
    }

    @Override
    public void preselectMerchantTabWithIndex(int tabIndex) {
        tabStrip.getTabAt(tabIndex).select();
    }

    @Override
    public void updateToolbarTitle(@Nullable DtlLocation dtlLocation) {
        if (dtlLocation == null || toolbar == null) return; // for safety reasons
        //
        TextView locationTitle = ButterKnife.<TextView>findById(toolbar, R.id.spinnerStyledTitle);
        TextView locationModeCaption = ButterKnife.<TextView>findById(toolbar, R.id.locationModeCaption);
        //
        if (locationTitle == null || locationModeCaption == null) return;
        //
        switch (dtlLocation.getLocationSourceType()) {
            case NEAR_ME:
            case EXTERNAL:
                locationTitle.setText(dtlLocation.getLongName());
                locationModeCaption.setVisibility(View.GONE);
                break;
            case FROM_MAP:
                if (dtlLocation.getLongName() == null) {
                    locationModeCaption.setVisibility(View.GONE);
                    locationTitle.setText(R.string.dtl_nearby_caption);
                } else {
                    locationModeCaption.setVisibility(View.VISIBLE);
                    locationTitle.setText(dtlLocation.getLongName());
                }
                break;
        }
    }

    private void initToolbar() {
        if (!tabletAnalytic.isTabletLandscape()) {
            toolbar.setNavigationIcon(R.drawable.ic_menu_hamburger);
        }
        toolbar.setNavigationOnClickListener(view -> ((MainActivity) getActivity()).openLeftDrawer());
        //
        ButterKnife.findById(toolbar, R.id.titleContainer).setOnClickListener(v -> {
            router.moveTo(Route.DTL_LOCATIONS, NavigationConfigBuilder.forFragment()
                    .backStackEnabled(true)
                    .containerId(R.id.dtl_container)
                    .data(new DtlLocationsBundle())
                    .fragmentManager(getParentFragment().getFragmentManager())
                    .build());
        });
    }

    @Override
    public void openDetails(String merchantId) {
        router.moveTo(Route.DTL_MERCHANT_DETAILS, NavigationConfigBuilder.forActivity()
                .data(new DtlMerchantDetailsBundle(merchantId, false))
                .toolbarConfig(ToolbarConfig.Builder.create().visible(false).build())
                .build());
    }

    @Override
    public void openLocationsWhenEmpty() {
        router.moveTo(Route.DTL_LOCATIONS, NavigationConfigBuilder.forFragment()
                .backStackEnabled(true)
                .data(new DtlLocationsBundle(true))
                .containerId(R.id.dtl_container)
                .fragmentManager(getParentFragment().getFragmentManager())
                .build());
    }

    @Override
    public boolean onApiError(ErrorResponse errorResponse) {
        SweetAlertDialog alertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.alert))
                .setContentText(errorResponse.getFirstMessage())
                .setConfirmText(getString(R.string.ok))
                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation);
        alertDialog.setCancelable(false);
        alertDialog.show();
        return true;
    }

    @Override
    public void onApiCallFailed() {
        //
    }

    @Override
    public void onDestroyView() {
        searchViewHelper.dropHelper();
        super.onDestroyView();
    }
}
