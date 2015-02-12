package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.techery.spares.loader.ContentLoader;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Activity;
import com.worldventures.dreamtrips.core.model.DateFilterItem;
import com.worldventures.dreamtrips.core.model.FilterModel;
import com.worldventures.dreamtrips.core.model.Region;
import com.worldventures.dreamtrips.core.model.ThemeHeaderModel;
import com.worldventures.dreamtrips.presentation.FiltersFragmentPM;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.cell.ActivityCell;
import com.worldventures.dreamtrips.view.cell.DateCell;
import com.worldventures.dreamtrips.view.cell.FiltersCell;
import com.worldventures.dreamtrips.view.cell.RegionCell;
import com.worldventures.dreamtrips.view.cell.ThemeHeaderCell;
import com.worldventures.dreamtrips.view.custom.EmptyRecyclerView;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Edward on 22.01.15.
 * filters fragment for right side nav drawer
 */
@Layout(R.layout.layout_filters)
public class FiltersFragment extends BaseFragment<FiltersFragmentPM> implements FiltersFragmentPM.View {

    @InjectView(R.id.recyclerViewRegions)
    EmptyRecyclerView recyclerView;
    @InjectView(R.id.progressBarFilters)
    ProgressBar progressBar;

    BaseArrayListAdapter<Object> arrayListAdapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(layoutManager);

        this.arrayListAdapter = new BaseArrayListAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        this.arrayListAdapter.registerCell(Region.class, RegionCell.class);
        this.arrayListAdapter.registerCell(FilterModel.class, FiltersCell.class);
        this.arrayListAdapter.registerCell(Activity.class, ActivityCell.class);
        this.arrayListAdapter.registerCell(ThemeHeaderModel.class, ThemeHeaderCell.class);
        //this.arrayListAdapter.registerCell(SoldOutModel.class, SoldOutCell.class);
        this.arrayListAdapter.registerCell(DateFilterItem.class, DateCell.class);

        this.recyclerView.setHasFixedSize(false);
        this.recyclerView.setAdapter(this.arrayListAdapter);
    }

    @OnClick(R.id.textViewApplyFilter)
    void applyFilter() {
        ((MainActivity) getActivity()).closeRightDrawer();
        getPresentationModel().acceptFilters();
    }

    @OnClick(R.id.textViewResetFilter)
    void resetFilter() {
        ((MainActivity) getActivity()).closeRightDrawer();
        getPresentationModel().resetFilters();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void dataSetChanged() {
        arrayListAdapter.notifyDataSetChanged();
    }

    public void refresh() {
        if (this.arrayListAdapter.getItemCount() == 0) {
            this.recyclerView.post(() ->
                            getPresentationModel().reload()
            );
        }
    }

    @Override
    protected FiltersFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new FiltersFragmentPM(this);
    }

    @Override
    public void addAll(List items) {
        arrayListAdapter.addItems(items);
    }

    @Override
    public void add(Object item) {

    }

    @Override
    public void add(int position, Object item) {

    }

    @Override
    public void clear() {
        arrayListAdapter.clear();
    }

    @Override
    public void replace(int position, Object item) {

    }

    @Override
    public void remove(int index) {

    }

    @Override
    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishLoading() {
        progressBar.setVisibility(View.GONE);

    }

}
