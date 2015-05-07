package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.DateFilterItem;
import com.worldventures.dreamtrips.modules.trips.model.FilterModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;
import com.worldventures.dreamtrips.modules.trips.model.ThemeHeaderModel;
import com.worldventures.dreamtrips.modules.trips.presenter.FiltersPresenter;
import com.worldventures.dreamtrips.modules.trips.view.cell.ActivityCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.DateCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.FiltersCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.RegionCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.ThemeHeaderCell;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.layout_filters)
public class FiltersFragment extends BaseFragment<FiltersPresenter> implements FiltersPresenter.View {

    @InjectView(R.id.recyclerViewRegions)
    protected EmptyRecyclerView recyclerView;
    @InjectView(R.id.progressBarFilters)
    protected ProgressBar progressBar;

    protected BaseArrayListAdapter<Object> arrayListAdapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(layoutManager);

        this.arrayListAdapter = new BaseArrayListAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        this.arrayListAdapter.registerCell(RegionModel.class, RegionCell.class);
        this.arrayListAdapter.registerCell(FilterModel.class, FiltersCell.class);
        this.arrayListAdapter.registerCell(ActivityModel.class, ActivityCell.class);
        this.arrayListAdapter.registerCell(ThemeHeaderModel.class, ThemeHeaderCell.class);
        this.arrayListAdapter.registerCell(DateFilterItem.class, DateCell.class);

        this.recyclerView.setHasFixedSize(false);
        this.recyclerView.setAdapter(this.arrayListAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.recyclerView.setAdapter(null);
    }

    @OnClick(R.id.textViewApplyFilter)
    void applyFilter() {
        ((MainActivity) getActivity()).closeRightDrawer();
        getPresenter().acceptFilters();
    }

    @OnClick(R.id.textViewResetFilter)
    void resetFilter() {
        ((MainActivity) getActivity()).closeRightDrawer();
        getPresenter().resetFilters();
    }

    @Override
    public void dataSetChanged() {
        arrayListAdapter.notifyDataSetChanged();
    }

    @Override
    public void startLoading() {
        if (arrayListAdapter.getItemCount() == 0)
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public BaseArrayListAdapter getAdapter() {
        return arrayListAdapter;
    }

    public void refresh() {
        this.recyclerView.post(() ->
                        getPresenter().fillData()
        );
    }

    @Override
    protected FiltersPresenter createPresenter(Bundle savedInstanceState) {
        return new FiltersPresenter(this);
    }
}