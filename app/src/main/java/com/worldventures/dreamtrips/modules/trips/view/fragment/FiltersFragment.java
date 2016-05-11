package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.adapter.BaseDelegateAdapter;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.activity.MainActivity;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.DateFilterItem;
import com.worldventures.dreamtrips.modules.trips.model.FilterFavoriteModel;
import com.worldventures.dreamtrips.modules.trips.model.FilterModel;
import com.worldventures.dreamtrips.modules.trips.model.FilterRecentlyAddedModel;
import com.worldventures.dreamtrips.modules.trips.model.FilterSoldOutModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionHeaderModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;
import com.worldventures.dreamtrips.modules.trips.model.ThemeHeaderModel;
import com.worldventures.dreamtrips.modules.trips.presenter.FiltersPresenter;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.DateCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.FavoritesCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.FilterRangeBarsCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.FilterRecentlyAddedCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.HeaderRegionCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.HeaderThemeCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.RegionCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.SoldOutCell;
import com.worldventures.dreamtrips.modules.trips.view.cell.filter.ThemeCell;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.layout_filters)
public class FiltersFragment extends BaseFragment<FiltersPresenter> implements FiltersPresenter.View {

    @InjectView(R.id.recyclerViewFilters)
    protected EmptyRecyclerView recyclerView;
    protected BaseDelegateAdapter<Object> arrayListAdapter;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        this.recyclerView.setLayoutManager(layoutManager);

        this.arrayListAdapter = new BaseDelegateAdapter<>(getActivity(), this);
        this.arrayListAdapter.registerCell(RegionModel.class, RegionCell.class);
        this.arrayListAdapter.registerCell(FilterModel.class, FilterRangeBarsCell.class);
        this.arrayListAdapter.registerCell(ActivityModel.class, ThemeCell.class);
        this.arrayListAdapter.registerCell(RegionHeaderModel.class, HeaderRegionCell.class);
        this.arrayListAdapter.registerCell(ThemeHeaderModel.class, HeaderThemeCell.class);
        this.arrayListAdapter.registerCell(DateFilterItem.class, DateCell.class);
        this.arrayListAdapter.registerCell(FilterSoldOutModel.class, SoldOutCell.class);
        this.arrayListAdapter.registerCell(FilterFavoriteModel.class, FavoritesCell.class);
        this.arrayListAdapter.registerCell(FilterRecentlyAddedModel.class, FilterRecentlyAddedCell.class);

        new FiltersCallbackHandler().init(arrayListAdapter, getPresenter());

        this.recyclerView.setHasFixedSize(false);
        this.recyclerView.setAdapter(this.arrayListAdapter);
    }

    @Override
    public void onDestroyView() {
        this.recyclerView.setAdapter(null);
        super.onDestroyView();
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
    public BaseArrayListAdapter getAdapter() {
        return arrayListAdapter;
    }

    @Override
    protected FiltersPresenter createPresenter(Bundle savedInstanceState) {
        return new FiltersPresenter();
    }
}