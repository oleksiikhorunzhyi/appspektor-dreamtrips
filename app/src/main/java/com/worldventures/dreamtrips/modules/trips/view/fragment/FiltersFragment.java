package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
import com.worldventures.core.ui.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.trips.model.filter.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.filter.RegionModel;
import com.worldventures.dreamtrips.modules.trips.model.filter.DateFilterItem;
import com.worldventures.dreamtrips.modules.trips.model.filter.FilterFavoriteModel;
import com.worldventures.dreamtrips.modules.trips.model.filter.FilterModel;
import com.worldventures.dreamtrips.modules.trips.model.filter.FilterRecentlyAddedModel;
import com.worldventures.dreamtrips.modules.trips.model.filter.FilterSoldOutModel;
import com.worldventures.dreamtrips.modules.trips.model.filter.RegionHeaderModel;
import com.worldventures.dreamtrips.modules.trips.model.filter.ThemeHeaderModel;
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
import com.worldventures.dreamtrips.social.ui.activity.SocialMainActivity;

import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.layout_filters)
public class FiltersFragment extends BaseFragment<FiltersPresenter> implements FiltersPresenter.View {

   @InjectView(R.id.recyclerViewFilters) EmptyRecyclerView recyclerView;
   @InjectView(R.id.progress) ProgressBar progressBar;
   @InjectView(R.id.error_container) View errorContainer;

   protected BaseDelegateAdapter<Object> arrayListAdapter;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);

      RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
      recyclerView.setLayoutManager(layoutManager);

      arrayListAdapter = new BaseDelegateAdapter<>(getActivity(), this);
      arrayListAdapter.registerCell(RegionModel.class, RegionCell.class);
      arrayListAdapter.registerCell(FilterModel.class, FilterRangeBarsCell.class);
      arrayListAdapter.registerCell(ActivityModel.class, ThemeCell.class);
      arrayListAdapter.registerCell(RegionHeaderModel.class, HeaderRegionCell.class);
      arrayListAdapter.registerCell(ThemeHeaderModel.class, HeaderThemeCell.class);
      arrayListAdapter.registerCell(DateFilterItem.class, DateCell.class);
      arrayListAdapter.registerCell(FilterSoldOutModel.class, SoldOutCell.class);
      arrayListAdapter.registerCell(FilterFavoriteModel.class, FavoritesCell.class);
      arrayListAdapter.registerCell(FilterRecentlyAddedModel.class, FilterRecentlyAddedCell.class);

      new FiltersCallbackHandler().init(arrayListAdapter, getPresenter());

      recyclerView.setHasFixedSize(false);
      recyclerView.setAdapter(arrayListAdapter);
   }

   @Override
   public void onDestroyView() {
      this.recyclerView.setAdapter(null);
      super.onDestroyView();
   }

   @OnClick(R.id.textViewApplyFilter)
   void applyFilter() {
      ((SocialMainActivity) getActivity()).closeRightDrawer();
      getPresenter().acceptFilters();
   }

   @OnClick(R.id.textViewResetFilter)
   void resetFilter() {
      ((SocialMainActivity) getActivity()).closeRightDrawer();
      getPresenter().resetFilters();
   }

   @OnClick(R.id.btn_retry)
   void retry() {
      hideErrorContainer();
      getPresenter().loadFilters();
   }

   @Override
   public void showProgress() {
      progressBar.setVisibility(View.VISIBLE);
   }

   @Override
   public void hideProgress() {
      progressBar.setVisibility(View.GONE);
   }

   @Override
   public void showErrorContainer() {
      errorContainer.setVisibility(View.VISIBLE);
   }

   @Override
   public void hideErrorContainer() {
      errorContainer.setVisibility(View.GONE);
   }

   @Override
   public void fillData(List data) {
      if (recyclerView != null) {
         recyclerView.setVisibility(View.VISIBLE);
      }
      arrayListAdapter.clear();
      arrayListAdapter.addItems(data);
   }

   @Override
   public void dataSetChanged() {
      arrayListAdapter.notifyDataSetChanged();
   }

   @Override
   protected FiltersPresenter createPresenter(Bundle savedInstanceState) {
      return new FiltersPresenter();
   }
}
