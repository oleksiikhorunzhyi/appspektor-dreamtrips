package com.worldventures.dreamtrips.modules.dtl.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForActivity;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.FilterableArrayListAdapter;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.dtl.event.DtlSearchPlaceRequestEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;
import com.worldventures.dreamtrips.modules.dtl.presenter.DtlPlacesListPresenter;
import com.worldventures.dreamtrips.modules.dtl.view.cell.DtlPlaceCell;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.InjectView;

@Layout(R.layout.fragment_dtl_places_list)
public class DtlPlacesListFragment
        extends BaseFragment<DtlPlacesListPresenter>
        implements DtlPlacesListPresenter.View {

    public static final String EXTRA_TYPE = "EXTRA_TYPE";

    @Inject
    @ForActivity
    Provider<Injector> injectorProvider;

    @InjectView(R.id.lv_items)
    EmptyRecyclerView recyclerView;
    @InjectView(R.id.swipe_container)
    protected SwipeRefreshLayout refreshLayout;
    @InjectView(R.id.emptyView)
    protected View emptyView;
    //
    BaseArrayListAdapter<DtlPlace> adapter;
    private WeakHandler weakHandler;

    RecyclerViewStateDelegate stateDelegate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weakHandler = new WeakHandler();
        stateDelegate = new RecyclerViewStateDelegate();
        stateDelegate.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        stateDelegate.saveStateIfNeeded(outState);
    }

    @Override
    protected DtlPlacesListPresenter createPresenter(Bundle savedInstanceState) {
        return new DtlPlacesListPresenter((DtlPlaceType) getArguments().getSerializable(EXTRA_TYPE));
    }


    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        stateDelegate.setRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new BaseArrayListAdapter<>(getActivity(), injectorProvider.get());
        adapter.registerCell(DtlPlace.class, DtlPlaceCell.class);
        recyclerView.setAdapter(adapter);
        recyclerView.setEmptyView(emptyView);
        //
        refreshLayout.setColorSchemeResources(R.color.theme_main_darker);
        // we use SwipeRefreshLayout only for loading indicator, so disable manual triggering by user
        refreshLayout.setEnabled(false);
    }

    @Override
    public void setItems(List<DtlPlace> places) {
        adapter.setItems(places);
        stateDelegate.restoreStateIfNeeded();
    }

    @Override
    public void showProgress() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(true);
        });
    }

    @Override
    public void hideProgress() {
        weakHandler.post(() -> {
            if (refreshLayout != null) refreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onDestroyView() {
        stateDelegate.onDestroyView();
        super.onDestroyView();
    }

}
