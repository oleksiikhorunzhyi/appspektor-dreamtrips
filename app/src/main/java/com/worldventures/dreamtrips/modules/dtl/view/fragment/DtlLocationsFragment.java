package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.Status;
import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.jakewharton.rxbinding.view.RxView;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragmentWithArgs;
import com.worldventures.dreamtrips.core.utils.ActivityResultDelegate;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.dtl.bundle.DtlLocationsBundle;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlLocationsPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationCell;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import icepick.State;
import timber.log.Timber;

@Layout(R.layout.fragment_dtl_locations)
@MenuResource(R.menu.menu_locations)
public class DtlLocationsFragment extends RxBaseFragmentWithArgs<DtlLocationsPresenter, DtlLocationsBundle>
        implements DtlLocationsPresenter.View, CellDelegate<DtlExternalLocation> {

    private static final int REQUEST_CHECK_SETTINGS = 1480;

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    @Inject
    ActivityResultDelegate activityResultDelegate;
    //
    @InjectView(R.id.locationsList)
    RecyclerView recyclerView;
    @InjectView(R.id.progress)
    View progressView;
    @InjectView(R.id.emptyMerchantsCaption)
    View emptyMerchantsCaption;
    @InjectView(R.id.autoDetectNearMe)
    Button autoDetectNearMe;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    //
    BaseDelegateAdapter adapter;
    //
    @State
    boolean shouldShowEmptyMerchantsCaption = false;

    @Override
    protected DtlLocationsPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlLocationsPresenter();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        initToolbar();
        //
        //
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources()
                .getDrawable(R.drawable.list_divider), true));
        if (getArgs().shouldShowEmptyMerchantsCaption || shouldShowEmptyMerchantsCaption) {
            this.shouldShowEmptyMerchantsCaption = true;
            emptyMerchantsCaption.setVisibility(View.VISIBLE);
        }
        //
        adapter = new BaseDelegateAdapter<DtlExternalLocation>(getActivity(), injectorProvider.get());
        adapter.registerCell(DtlExternalLocation.class, DtlLocationCell.class);
        adapter.registerDelegate(DtlExternalLocation.class, this);
        //
        recyclerView.setAdapter(adapter);
        //
        bindNearMeButton();
    }

    private void initToolbar() {
        boolean hasBackStack = getParentFragment().getChildFragmentManager()
                .findFragmentByTag(Route.DTL_MERCHANTS_HOLDER.getClazzName()) != null;
        toolbar.setTitle(Route.DTL_LOCATIONS.getTitleRes());
        toolbar.inflateMenu(R.menu.menu_locations);
        if (hasBackStack) {
            toolbar.setNavigationIcon(R.drawable.back_icon);
            toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        } else if (!tabletAnalytic.isTabletLandscape()) {
            toolbar.setNavigationIcon(R.drawable.ic_menu_hamburger);
            toolbar.setNavigationOnClickListener(view -> ((MainActivity) getActivity()).openLeftDrawer());
        }
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                navigateToSearch();
                return true;
            }
            return super.onOptionsItemSelected(item);
        });
    }

    @Override
    public void locationResolutionRequired(Status status) {
        try {
            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException th) {
            Timber.e(th, "Error opening settings activity.");
        }
    }

    private void bindNearMeButton() {
        bind(RxView.clicks(autoDetectNearMe))
                .throttleFirst(3L, TimeUnit.SECONDS)
                .subscribe(aVoid -> getPresenter().loadNearMeRequested());
    }

    @Override
    public void onCellClicked(DtlExternalLocation model) {
        tryHideSoftInput();
        getPresenter().onLocationSelected(model);
    }

    @Override
    public void setItems(List<DtlExternalLocation> dtlExternalLocations) {
        hideProgress();
        //
        adapter.clear();
        adapter.addItems(dtlExternalLocations);
    }

    @Override
    public void hideNearMeButton() {
        autoDetectNearMe.setVisibility(View.GONE);
    }

    @Override
    public void showProgress() {
        progressView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressView.setVisibility(View.GONE);
    }

    @Override
    public boolean onApiError(ErrorResponse errorResponse) {
        return false;
    }

    @Override
    public void onApiCallFailed() {
        progressView.setVisibility(View.GONE);
    }

    @Override
    public void navigateToMerchants() {
        router.moveTo(Route.DTL_MERCHANTS_HOLDER, NavigationConfigBuilder.forFragment()
                .containerId(R.id.dtl_container)
                .fragmentManager(getFragmentManager())
                .backStackEnabled(false)
                .clearBackStack(true)
                .build());
    }

    public void navigateToSearch() {
        router.moveTo(Route.DTL_LOCATIONS_SEARCH, NavigationConfigBuilder.forFragment()
                .containerId(R.id.dtl_container)
                .fragmentManager(getFragmentManager())
                .backStackEnabled(true)
                .build());
    }

    public void activityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    getPresenter().onLocationResolutionGranted();
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    getPresenter().onLocationResolutionDenied();
                    break;
            }
            activityResultDelegate.clear();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        activityResult(activityResultDelegate.getRequestCode(), activityResultDelegate.getResultCode());
    }
}
