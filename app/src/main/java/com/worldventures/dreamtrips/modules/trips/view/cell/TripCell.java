package com.worldventures.dreamtrips.modules.trips.view.cell;

import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.view.util.TripFeedViewInjector;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_trip)
public class TripCell extends BaseAbstractDelegateCell<TripModel, TripCellDelegate> {

   private TripFeedViewInjector tripFeedViewInjector;

   public TripCell(View view) {
      super(view);
      tripFeedViewInjector = new TripFeedViewInjector(itemView);
   }

   @Override
   protected void syncUIStateWithModel() {
      tripFeedViewInjector.initTripData(getModelObject());
   }

   @OnClick(R.id.imageViewLike)
   void onLike() {
      cellDelegate.onLikeClicked(getModelObject());
   }

   @OnClick(R.id.imageViewAddToBucket)
   void onAddToBucket() {
      cellDelegate.onAddToBucketClicked(getModelObject());
   }

   @OnClick(R.id.layoutInfo)
   void onInfoClick() {
      actionItemClick();
   }

   @OnClick(R.id.itemLayout)
   void actionItemClick() {
      cellDelegate.onCellClicked(getModelObject());
   }

   @Override
   public boolean shouldInject() {
      return false;
   }
}
