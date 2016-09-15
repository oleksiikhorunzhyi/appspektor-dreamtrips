package com.worldventures.dreamtrips.modules.dtl.view.cell;


import android.view.View;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocationType;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_dtl_location)
public class DtlLocationCell extends AbstractDelegateCell<DtlExternalLocation, CellDelegate<DtlExternalLocation>> {

   @InjectView(R.id.city_state) TextView city;

   public DtlLocationCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      StringBuilder sb = new StringBuilder();
      sb.append(getModelObject().getLongName());
      Queryable.from(getModelObject().getLocatedIn())
            .filter(temp -> temp.getType() != DtlLocationType.METRO)
            .sort(DtlExternalLocation.CATEGORY_COMPARATOR)
            .forEachR(tempLocation -> {
               sb.append(", ");
               sb.append(tempLocation.getLongName());
            });

      city.setText(sb.toString());
      city.setCompoundDrawablesWithIntrinsicBounds(getModelObject().getType() == DtlLocationType.CITY ? R.drawable.city_icon : R.drawable.metro_area_icon, 0, 0, 0);
   }

   @OnClick(R.id.dtlLocationCellRoot)
   void cellClicked() {
      cellDelegate.onCellClicked(getModelObject());
   }

   @Override
   public void prepareForReuse() {
   }
}
