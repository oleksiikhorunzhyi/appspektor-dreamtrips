package com.worldventures.dreamtrips.modules.trips.view.fragment;

import com.worldventures.core.ui.view.adapter.BaseDelegateAdapter;
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

public class FiltersCallbackHandler {

   public void init(BaseDelegateAdapter adapter, FiltersPresenter presenter) {
      setDateFilterItemDelegate(adapter, presenter);
      setFilterRecentlyAddedModelDelegate(adapter, presenter);
      setFilterFavoriteModelDelegate(adapter, presenter);
      setFilterSoldOutModelDelegate(adapter, presenter);
      setHeaderThemeCellDelegate(adapter, presenter);
      setRegionHeaderModelDelegate(adapter, presenter);
      setFilterModelDelegate(adapter, presenter);
      setRegionModelDelegate(adapter, presenter);
      setActivityModelDelegate(adapter, presenter);
   }

   protected void setActivityModelDelegate(BaseDelegateAdapter adapter, final FiltersPresenter presenter) {
      adapter.registerDelegate(ActivityModel.class, new ThemeCell.Delegate() {
         @Override
         public void onCellClicked(ActivityModel model) {
            //do nothing
         }

         @Override
         public void onThemeSetChangedEvent() {
            presenter.onThemeSetChangedEvent();
         }
      });
   }

   protected void setRegionModelDelegate(BaseDelegateAdapter adapter, final FiltersPresenter presenter) {
      adapter.registerDelegate(RegionModel.class, new RegionCell.Delegate() {
         @Override
         public void onCellClicked(RegionModel model) {
            //do nothing
         }

         @Override
         public void onRegionSetChangedEvent() {
            presenter.onRegionSetChangedEvent();
         }
      });
   }

   protected void setFilterModelDelegate(BaseDelegateAdapter adapter, final FiltersPresenter presenter) {
      adapter.registerDelegate(FilterModel.class, new FilterRangeBarsCell.Delegate() {
         @Override
         public void onCellClicked(FilterModel model) {
            //do nothing
         }

         @Override
         public void rangeBarDurationEvent(int minNights, int maxNights) {
            presenter.onRangeBarDurationEvent(minNights, maxNights);
         }

         @Override
         public void rangeBarPriceEvent(double minPrice, double maxPrice) {
            presenter.onRangeBarPriceEvent(minPrice, maxPrice);
         }
      });
   }

   protected void setRegionHeaderModelDelegate(BaseDelegateAdapter adapter, final FiltersPresenter presenter) {
      adapter.registerDelegate(RegionHeaderModel.class, new HeaderRegionCell.Delegate() {

         @Override
         public void toggleVisibility() {
            presenter.toggleRegionVisibility();
         }

         public void onCellClicked(RegionHeaderModel model) {
            //do nothing
         }

         @Override
         public void onCheckBoxAllRegionsPressedEvent(boolean isChecked) {
            presenter.onCheckBoxAllRegionsPressedEvent(isChecked);
         }
      });
   }

   protected void setHeaderThemeCellDelegate(BaseDelegateAdapter adapter, final FiltersPresenter presenter) {
      adapter.registerDelegate(ThemeHeaderModel.class, new HeaderThemeCell.Delegate() {
         @Override
         public void onCellClicked(ThemeHeaderModel model) {
            //do nothing
         }

         @Override
         public void onCheckBoxAllThemePressedEvent(boolean isChecked) {
            presenter.onCheckBoxAllThemePressedEvent(isChecked);
         }

         @Override
         public void toggleVisibility() {
            presenter.toggleThemeVisibility();
         }
      });
   }

   protected void setFilterSoldOutModelDelegate(BaseDelegateAdapter adapter, final FiltersPresenter presenter) {
      adapter.registerDelegate(FilterSoldOutModel.class, new SoldOutCell.Delegate() {
         @Override
         public void onCellClicked(FilterSoldOutModel model) {
            //do nothing
         }

         @Override
         public void onFilterShowSoldOutEvent(boolean enabled) {
            presenter.onFilterShowSoldOutEvent(enabled);
         }
      });
   }

   protected void setFilterFavoriteModelDelegate(BaseDelegateAdapter adapter, final FiltersPresenter presenter) {
      adapter.registerDelegate(FilterFavoriteModel.class, new FavoritesCell.Delegate() {
         @Override
         public void onCellClicked(FilterFavoriteModel model) {
            //do nothing
         }

         @Override
         public void onFilterShowFavoritesEvent(boolean enabled) {
            presenter.onFilterShowFavoritesEvent(enabled);
         }
      });
   }

   protected void setFilterRecentlyAddedModelDelegate(BaseDelegateAdapter adapter, final FiltersPresenter presenter) {
      adapter.registerDelegate(FilterRecentlyAddedModel.class, new FilterRecentlyAddedCell.Delegate() {
         @Override
         public void onCellClicked(FilterRecentlyAddedModel model) {
            //do nothing
         }

         @Override
         public void onFilterShowRecentlyAddedEvent(boolean enabled) {
            presenter.onFilterShowRecentlyAddedEvent(enabled);
         }
      });
   }

   protected void setDateFilterItemDelegate(BaseDelegateAdapter adapter, final FiltersPresenter presenter) {
      adapter.registerDelegate(DateFilterItem.class, new DateCell.Delegate() {
         @Override
         public void onCellClicked(DateFilterItem model) {
            //do nothing
         }

         @Override
         public void onDatesChanged(DateFilterItem item) {
            presenter.onDatesChanged(item);
         }
      });
   }
}
