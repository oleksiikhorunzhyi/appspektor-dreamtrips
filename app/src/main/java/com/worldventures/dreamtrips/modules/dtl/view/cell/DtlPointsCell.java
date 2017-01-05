package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_offer_points)
public class DtlPointsCell extends AbstractDelegateCell<Offer, CellDelegate<Offer>> {

   public DtlPointsCell(View view) {
      super(view);
   }

   @OnClick(R.id.points_view)
   protected void onPerkClick() {
      cellDelegate.onCellClicked(getModelObject());
   }

   @Override
   protected void syncUIStateWithModel() {
   }

   @Override
   public void prepareForReuse() {
   }
}
