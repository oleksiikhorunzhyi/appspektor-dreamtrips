package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.h6ah4i.android.widget.advrecyclerview.decoration.SimpleListDividerDecorator;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.api.error.ErrorResponse;
import com.worldventures.dreamtrips.core.navigation.BackStackDelegate;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.navigation.router.NavigationConfigBuilder;
import com.worldventures.dreamtrips.core.rx.RxBaseFragment;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlLocationsSearchPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationCell;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;
import rx.Observable;

@Layout(R.layout.fragment_dtl_locations_search)
@MenuResource(R.menu.menu_locations_search)
public class DtlLocationsSearchFragment extends RxBaseFragment<DtlLocationsSearchPresenter>
        implements DtlLocationsSearchPresenter.View, CellDelegate<DtlExternalLocation> {

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;
    @Inject
    BackStackDelegate backStackDelegate;
    //
    @InjectView(R.id.locationsList)
    RecyclerView recyclerView;
    @InjectView(R.id.progress)
    View progressView;
    @InjectView(R.id.default_caption)
    View defaultCaption;
    @InjectView(R.id.toolbar_actionbar)
    Toolbar toolbar;
    //
    BaseDelegateAdapter adapter;
    MenuItem searchItem;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        initToolbar();
        //
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new SimpleListDividerDecorator(getResources()
                .getDrawable(R.drawable.list_divider), true));
        //
        adapter = new BaseDelegateAdapter<DtlExternalLocation>(getActivity(), injectorProvider.get());
        adapter.registerCell(DtlExternalLocation.class, DtlLocationCell.class);
        adapter.registerDelegate(DtlExternalLocation.class, this);
        //
        recyclerView.setAdapter(adapter);
        //
        configureSearch();
    }

    private void initToolbar() {
        toolbar.setTitle(Route.DTL_LOCATIONS.getTitleRes());
        toolbar.inflateMenu(R.menu.menu_locations_search);
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(view -> getPresenter().searchClosed());
    }

    private void configureSearch() {
        searchItem = toolbar.getMenu().findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setIconified(false);
            // line below is magic - prevents empty string to be sent as query during screen creation
            searchView.setQuery("", false);
            bind(RxSearchView.queryTextChanges(searchView).skip(1))
                    .flatMap(charSequence -> Observable.just(charSequence.toString()))
                    .subscribe(getPresenter()::search);
            //
            MenuItemCompat.expandActionView(searchItem);
            MenuItemCompat.setOnActionExpandListener(searchItem, searchViewExpandListener);
        }
    }

    @Override
    public void setItems(List<DtlExternalLocation> dtlExternalLocations) {
        progressView.setVisibility(View.GONE);
        adapter.clearAndUpdateItems(dtlExternalLocations);
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
    public void showDefaultCaption(boolean visible) {
        defaultCaption.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void navigateToNearby() {
        getFragmentManager().popBackStack();
//        router.moveTo(Route.DTL_LOCATIONS, NavigationConfigBuilder.forFragment()
//                .containerId(R.id.dtl_container)
//                .fragmentManager(getFragmentManager())
//                .data(new DtlLocationsBundle())
//                .backStackEnabled(false)
//                .build());
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

    @Override
    public void onCellClicked(DtlExternalLocation model) {
        tryHideSoftInput();
        getPresenter().onLocationSelected(model);
    }

    @Override
    public void onResume() {
        super.onResume();
        backStackDelegate.setListener(this::onBackPressed);
    }

    @Override
    public void onPause() {
        backStackDelegate.clearListener();
        super.onPause();
    }

    private MenuItemCompat.OnActionExpandListener searchViewExpandListener =
            new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true; // do nothing - always expanded
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    getPresenter().searchClosed();
                    return true;
                }
            };

    private boolean onBackPressed() {
        getPresenter().searchClosed();
        return true;
    }

    @Override
    protected DtlLocationsSearchPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlLocationsSearchPresenter();
    }

    @Override
    public boolean onApiError(ErrorResponse errorResponse) {
        return false;
    }

    @Override
    public void onApiCallFailed() {
    }

    @Override
    public void onDestroyView() {
        MenuItemCompat.setOnActionExpandListener(searchItem, null);
        super.onDestroyView();
    }
}
