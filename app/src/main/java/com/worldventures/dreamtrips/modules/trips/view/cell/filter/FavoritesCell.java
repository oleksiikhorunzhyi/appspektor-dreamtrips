package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.model.FilterFavoriteModel;

@Layout(R.layout.adapter_item_filter_one_checkbox)
public class FavoritesCell extends BoolCell<FilterFavoriteModel, FavoritesCell.Delegate> {

   public FavoritesCell(View view) {
      super(view);
   }

   @Override
   public int getTitle() {
      return R.string.filters_show_favorite;
   }

   @Override
   public void sendEvent(boolean b) {
      cellDelegate.onFilterShowFavoritesEvent(b);
   }

   public interface Delegate extends CellDelegate<FilterFavoriteModel> {
      void onFilterShowFavoritesEvent(boolean enabled);
   }
}
