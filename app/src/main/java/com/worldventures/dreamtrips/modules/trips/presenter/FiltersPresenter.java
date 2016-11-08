package com.worldventures.dreamtrips.modules.trips.presenter;

import android.os.Bundle;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.api.janet.command.TripsFilterDataCommand;
import com.worldventures.dreamtrips.modules.common.delegate.QueryTripsFilterDataInteractor;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.delegate.ResetFilterEventDelegate;
import com.worldventures.dreamtrips.modules.trips.delegate.TripFilterEventDelegate;
import com.worldventures.dreamtrips.modules.trips.model.ActivityModel;
import com.worldventures.dreamtrips.modules.trips.model.RegionModel;
import com.worldventures.dreamtrips.modules.trips.model.TripsFilterDataAnalyticsWrapper;
import com.worldventures.dreamtrips.modules.trips.model.filter.DateFilterItem;
import com.worldventures.dreamtrips.modules.trips.model.filter.FilterFavoriteModel;
import com.worldventures.dreamtrips.modules.trips.model.filter.FilterModel;
import com.worldventures.dreamtrips.modules.trips.model.filter.FilterRecentlyAddedModel;
import com.worldventures.dreamtrips.modules.trips.model.filter.FilterSoldOutModel;
import com.worldventures.dreamtrips.modules.trips.model.filter.RegionHeaderModel;
import com.worldventures.dreamtrips.modules.trips.model.filter.ThemeHeaderModel;
import com.worldventures.dreamtrips.util.TripsFilterData;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;

public class FiltersPresenter extends Presenter<FiltersPresenter.View> {

   @Inject SnappyRepository db;
   @Inject TripFilterEventDelegate tripFilterEventDelegate;
   @Inject ResetFilterEventDelegate resetFilterEventDelegate;
   @Inject QueryTripsFilterDataInteractor queryTripsFilterDataInteractor;

   @State ArrayList<RegionModel> regions = new ArrayList<>();
   @State ArrayList<ActivityModel> parentActivities = new ArrayList<>();

   @State FilterModel filterModel;
   @State ThemeHeaderModel themeHeaderModel;
   @State RegionHeaderModel regionHeaderModel;
   @State FilterSoldOutModel soldOutModel;
   @State FilterRecentlyAddedModel recentlyAddedModel;
   @State FilterFavoriteModel favoriteModel;
   @State DateFilterItem dateFilterItem;
   @State TripsFilterData tripFilterData;

   @Override
   public void restoreInstanceState(Bundle savedState) {
      super.restoreInstanceState(savedState);
      if (savedState == null) {
         filterModel = new FilterModel();
         dateFilterItem = new DateFilterItem();
         themeHeaderModel = new ThemeHeaderModel();
         soldOutModel = new FilterSoldOutModel();
         favoriteModel = new FilterFavoriteModel();
         recentlyAddedModel = new FilterRecentlyAddedModel();
         regionHeaderModel = new RegionHeaderModel();
      }
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      subscribeToFiltersLoading();
      if (tripFilterData == null) {
         tripFilterData = TripsFilterData.createDefault(db);
      }
      loadFilters();
      observeResetFilters();
   }

   private void subscribeToFiltersLoading() {
      queryTripsFilterDataInteractor.pipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<TripsFilterDataCommand>().onSuccess(tripsFilterDataCommand -> {
               view.hideProgress();
               parentActivities.clear();
               parentActivities.addAll(getParentActivities(tripsFilterDataCommand.getResult().first));
               regions.clear();
               regions.addAll(tripsFilterDataCommand.getResult().second);
               fillData();
            }).onFail((tripsFilterDataCommand, throwable) -> {
               view.hideProgress();
               view.showErrorContainer();
            }));
   }

   private void observeResetFilters() {
      resetFilterEventDelegate.getObservable()
            .compose(bindViewToMainComposer())
            .subscribe(object -> resetFilters());
   }

   public void loadFilters() {
      view.showProgress();
      queryTripsFilterDataInteractor.pipe().send(new TripsFilterDataCommand());
   }

   public void fillData() {
      if (regions != null && parentActivities != null) {
         List<Object> data = new ArrayList<>();
         data.clear();
         data.add(dateFilterItem);
         data.add(filterModel);
         data.add(soldOutModel);
         data.add(favoriteModel);
         data.add(recentlyAddedModel);

         data.add(regionHeaderModel);
         if (!regionHeaderModel.isHide()) {
            data.addAll(regions);
         }

         data.add(themeHeaderModel);

         if (!themeHeaderModel.isHide()) {
            data.addAll(parentActivities);
         }

         view.fillData(data);
      }
   }

   public void setRegionsChecked(boolean isChecked) {
      if (regions != null) {
         for (RegionModel region : regions) {
            region.setChecked(isChecked);
         }
      }
   }

   public void setThemesChecked(boolean isChecked) {
      if (parentActivities != null) {
         for (ActivityModel activity : parentActivities) {
            activity.setChecked(isChecked);
         }
      }
   }

   public void acceptFilters() {
      tripFilterEventDelegate.post(tripFilterData);
      TrackingHelper.actionFilterTrips(new TripsFilterDataAnalyticsWrapper(tripFilterData));
   }

   public void resetFilters() {
      dateFilterItem.reset();
      filterModel.reset();
      themeHeaderModel.setChecked(true);
      regionHeaderModel.setChecked(true);
      soldOutModel.setActive(false);
      favoriteModel.setActive(false);
      recentlyAddedModel.setActive(false);
      setRegionsChecked(true);
      setThemesChecked(true);
      view.dataSetChanged();
      //
      tripFilterData = TripsFilterData.createDefault(db);
      tripFilterEventDelegate.post(tripFilterData);
      TrackingHelper.actionFilterTrips(new TripsFilterDataAnalyticsWrapper(tripFilterData));
   }

   private List<ActivityModel> getParentActivities(List<ActivityModel> activities) {
      return Queryable.from(activities).filter(ActivityModel::isParent).toList();
   }

   public void onRangeBarDurationEvent(int minNights, int maxNights) {
      tripFilterData.setMinNights(minNights);
      tripFilterData.setMaxNights(maxNights);
   }

   public void onRangeBarPriceEvent(double minPrice, double maxPrice) {
      tripFilterData.setMinPrice(minPrice);
      tripFilterData.setMaxPrice(maxPrice);
   }

   public void toggleThemeVisibility() {
      themeHeaderModel.setHide(!themeHeaderModel.isHide());
      fillData();
   }

   public void toggleRegionVisibility() {
      regionHeaderModel.setHide(!regionHeaderModel.isHide());
      fillData();
   }

   public void onFilterShowSoldOutEvent(boolean isSoldOut) {
      tripFilterData.setShowSoldOut(isSoldOut);
   }

   public void onFilterShowFavoritesEvent(boolean isSoldOut) {
      tripFilterData.setShowFavorites(isSoldOut);
   }

   public void onFilterShowRecentlyAddedEvent(boolean enabled) {
      tripFilterData.setShowRecentlyAdded(enabled);
   }

   public void onCheckBoxAllRegionsPressedEvent(boolean isChecked) {
      setRegionsChecked(isChecked);
      tripFilterData.setAllRegions(regions);
      view.dataSetChanged();
   }

   public void onCheckBoxAllThemePressedEvent(boolean isChecked) {
      setThemesChecked(isChecked);
      tripFilterData.setAllParentActivities(parentActivities);
      view.dataSetChanged();
   }

   public void onThemeSetChangedEvent() {
      boolean allIsChecked = true;
      for (ActivityModel activity : parentActivities) {
         if (!activity.isChecked()) {
            allIsChecked = false;
            break;
         }
      }
      themeHeaderModel.setChecked(allIsChecked);
      tripFilterData.setAllParentActivities(parentActivities);
      view.dataSetChanged();
   }

   public void onRegionSetChangedEvent() {
      boolean allIsChecked = true;
      for (RegionModel region : regions) {
         if (!region.isChecked()) {
            allIsChecked = false;
            break;
         }
      }
      regionHeaderModel.setChecked(allIsChecked);
      tripFilterData.setAllRegions(regions);
      view.dataSetChanged();
   }

   public void onDatesChanged(DateFilterItem item) {
      tripFilterData.setStartDate(item.getStartDate());
      tripFilterData.setEndDate(item.getEndDate());
   }

   public interface View extends Presenter.View {

      void showProgress();

      void hideProgress();

      void showErrorContainer();

      void hideErrorContainer();

      void fillData(List data);

      void dataSetChanged();
   }
}
