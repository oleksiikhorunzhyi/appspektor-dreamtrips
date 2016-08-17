package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;

@Layout(R.layout.adapter_item_nearby_header)
public class DtlNearbyHeaderCell extends AbstractCell<DtlNearbyHeaderCell.NearbyHeaderModel> {

   public DtlNearbyHeaderCell(View view) {
      super(view);
   }

   public static final class NearbyHeaderModel {

      private NearbyHeaderModel() {
      }

      public static final NearbyHeaderModel INSTANCE = new NearbyHeaderModel();
   }

   @Override
   protected void syncUIStateWithModel() {

   }

   @Override
   public void prepareForReuse() {

   }
}
