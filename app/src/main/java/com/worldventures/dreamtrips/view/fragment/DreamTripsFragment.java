package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.presentation.DreamTripsFragmentPM;
import com.worldventures.dreamtrips.view.cell.TripCell;
import com.worldventures.dreamtrips.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.view.custom.RecyclerItemClickListener;

import java.util.List;

import butterknife.InjectView;

@Layout(R.layout.fragment_dream_trips)
public class DreamTripsFragment extends BaseFragment<DreamTripsFragmentPM> implements DreamTripsFragmentPM.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.recyclerViewTrips)
    EmptyRecyclerView recyclerView;

    @InjectView(R.id.ll_empty_view)
    ViewGroup emptyView;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;

    BaseArrayListAdapter<Trip> adapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setEmptyView(emptyView);

        this.adapter = new BaseArrayListAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        this.adapter.registerCell(Trip.class, TripCell.class);

        this.recyclerView.setAdapter(adapter);

        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

        this.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), (view1, position) -> this.getPresentationModel().onItemClick(position))
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.adapter.getItemCount() == 0) {
            this.refreshLayout.post(() -> {
                this.refreshLayout.setRefreshing(true);
                this.getPresentationModel().loadTrips();
            });
        }
    }

    @Override
    public void onRefresh() {
        this.getPresentationModel().loadTrips();
    }

    @Override
    public void setTrips(List<Trip> trips) {
        this.adapter.addItems(trips);
        this.refreshLayout.setRefreshing(false);
    }

    @Override
    public void clearAdapter() {
        this.adapter.clear();
    }

    @Override
    protected DreamTripsFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new DreamTripsFragmentPM(this);
    }
}
