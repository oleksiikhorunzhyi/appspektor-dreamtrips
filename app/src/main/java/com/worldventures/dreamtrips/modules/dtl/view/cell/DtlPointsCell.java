package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.offer.Offer;

import butterknife.OnClick;

@Layout(R.layout.adapter_item_offer_points)
public class DtlPointsCell extends BaseAbstractDelegateCell<Offer, CellDelegate<Offer>> {

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
