package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ProgressBar;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.dtl.bundle.PlacesBundle;
import com.worldventures.dreamtrips.modules.dtl.event.LocationClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlLocationsPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlLocationCell;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.fragment_dtl_locations)
public class DtlLocationsFragment extends BaseFragment<DtlLocationsPresenter> implements DtlLocationsPresenter.View {

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    BaseArrayListAdapter<DtlLocation> adapter;

    @InjectView(R.id.locationsList)
    protected EmptyRecyclerView recyclerView;

    @InjectView(R.id.progressBarImage)
    ProgressBar progressBar;

    @Override
    protected DtlLocationsPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlLocationsPresenter();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BaseArrayListAdapter<>(getActivity(), injectorProvider);
        adapter.registerCell(DtlLocation.class, DtlLocationCell.class);
        recyclerView.setAdapter(adapter);
    }

    public void onEvent(LocationClickedEvent event) {
        getPresenter().onLocationClicked(event.getLocation());
    }

    @Override
    public BaseArrayListAdapter<DtlLocation> getAdapter() {
        return adapter;
    }

    @Override
    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showSearch() {

    }

    @Override
    public void openLocation(PlacesBundle bundle) {
        NavigationBuilder.create()
                .with(fragmentCompass)
                .data(bundle)
                .move(Route.DTL_PLACES_LIST);
    }
}
