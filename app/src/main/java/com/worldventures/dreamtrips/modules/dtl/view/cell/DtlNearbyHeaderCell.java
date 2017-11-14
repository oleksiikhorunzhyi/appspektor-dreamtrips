package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;

import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractCell;

@Layout(R.layout.adapter_item_nearby_header)
public class DtlNearbyHeaderCell extends BaseAbstractCell<DtlNearbyHeaderCell.NearbyHeaderModel> {

   public DtlNearbyHeaderCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      //do nothing
   }

   @Override
   public void prepareForReuse() {
      //do nothing
   }

   public static final class NearbyHeaderModel {

      public static final NearbyHeaderModel INSTANCE = new NearbyHeaderModel();

      private NearbyHeaderModel() {
      }
   }
}
