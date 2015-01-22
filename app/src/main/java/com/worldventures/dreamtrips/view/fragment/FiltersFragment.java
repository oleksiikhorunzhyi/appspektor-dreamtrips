package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.FilterModel;
import com.worldventures.dreamtrips.core.model.Region;
import com.worldventures.dreamtrips.presentation.FiltersFragmentPM;
import com.worldventures.dreamtrips.view.cell.FiltersCell;
import com.worldventures.dreamtrips.view.cell.RegionCell;
import com.worldventures.dreamtrips.view.custom.EmptyRecyclerView;

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

    BaseArrayListAdapter<Object> arrayListAdapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(layoutManager);

        this.arrayListAdapter = new BaseArrayListAdapter<>(getActivity(), (com.techery.spares.module.Injector) getActivity());
        this.arrayListAdapter.registerCell(Region.class, RegionCell.class);
        this.arrayListAdapter.registerCell(FilterModel.class, FiltersCell.class);

        this.arrayListAdapter.setContentLoader(getPresentationModel().getRegionController());
        this.recyclerView.setAdapter(this.arrayListAdapter);
        this.recyclerView.setHasFixedSize(true);
    }

    @OnClick(R.id.textViewApplyFilter)
    void applyFilter() {
        getPresentationModel().acceptFilters();
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
            this.recyclerView.post(() -> {
                getPresentationModel().getRegionController().reload();
            });
        }
    }

    @Override
    protected FiltersFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new FiltersFragmentPM(this);
    }
}
