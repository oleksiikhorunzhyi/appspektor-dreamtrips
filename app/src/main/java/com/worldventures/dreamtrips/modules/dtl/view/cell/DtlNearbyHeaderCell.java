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
