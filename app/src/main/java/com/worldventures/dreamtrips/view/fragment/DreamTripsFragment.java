package com.worldventures.dreamtrips.view.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.techery.spares.loader.ContentLoader;
import com.techery.spares.module.Annotations.Global;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.presentation.DreamTripsFragmentPM;
import com.worldventures.dreamtrips.utils.busevents.LikeTripEvent;
import com.worldventures.dreamtrips.utils.busevents.TouchTripEvent;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.cell.TripCell;
import com.worldventures.dreamtrips.view.custom.EmptyRecyclerView;

import java.util.List;

import javax.inject.Inject;

import butterknife.InjectView;
import de.greenrobot.event.EventBus;

@Layout(R.layout.fragment_dream_trips)
@MenuResource(R.menu.menu_dream_trips)
public class DreamTripsFragment extends BaseFragment<DreamTripsFragmentPM> implements DreamTripsFragmentPM.View, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.recyclerViewTrips)
    EmptyRecyclerView recyclerView;

    @InjectView(R.id.ll_empty_view)
    ViewGroup emptyView;

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout refreshLayout;

    @Inject
    @Global
    EventBus eventBus;

    BaseArrayListAdapter<Trip> adapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());

        this.recyclerView.setLayoutManager(layoutManager);
        this.recyclerView.setEmptyView(emptyView);

        this.adapter = new BaseArrayListAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        this.adapter.registerCell(Trip.class, TripCell.class);
        this.adapter.setContentLoader(getPresentationModel().getTripsController());

        this.recyclerView.setAdapter(adapter);

        this.refreshLayout.setOnRefreshListener(this);
        this.refreshLayout.setColorSchemeResources(R.color.theme_main_darker);

        getPresentationModel().getTripsController().getContentLoaderObserver().registerObserver(new ContentLoader.ContentLoadingObserving<List<Trip>>() {
            @Override
            public void onStartLoading() {
                refreshLayout.setRefreshing(true);
            }

            @Override
            public void onFinishLoading(List<Trip> result) {
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable throwable) {
                refreshLayout.setRefreshing(false);
                ((MainActivity) getActivity()).informUser(getString(R.string.smth_went_wrong));
            }
        });

        eventBus.register(this);
    }

    public void onEvent(LikeTripEvent likeTripEvent) {
        likeTripEvent.getTrip();
    }

    public void onEvent(TouchTripEvent event) {
        getPresentationModel().onItemClick(event.getPosition());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                this.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                this.recyclerView.requestLayout();
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                this.recyclerView.requestLayout();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.adapter.getItemCount() == 0) {
            this.refreshLayout.post(() -> {
                getPresentationModel().getTripsController().reload();
            });
        }
    }

    @Override
    public void onRefresh() {
        getPresentationModel().getTripsController().reload();
    }

    @Override
    protected DreamTripsFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new DreamTripsFragmentPM(this);
    }
}
