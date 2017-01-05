package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.locations.model.LocationType;
import com.worldventures.dreamtrips.modules.dtl.helper.comparator.LocationComparator;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_dtl_location_search)
public class DtlLocationSearchCell extends AbstractDelegateCell<DtlLocation, CellDelegate<DtlLocation>> {

   @InjectView(R.id.locationName) TextView locationName;

   public DtlLocationSearchCell(View view) {
      super(view);
   }

   @Override
   protected void syncUIStateWithModel() {
      StringBuilder sb = new StringBuilder();
      sb.append(getModelObject().longName());
      Queryable.from(getModelObject().locatedIn())
            .filter(temp -> (temp.type() != LocationType.METRO))
            .sort(LocationComparator.CATEGORY_COMPARATOR)
            .forEachR(tempLocation -> {
               sb.append(", ");
               sb.append(tempLocation.longName());
            });
      locationName.setText(sb.toString());
   }

   @OnClick(R.id.locationName)
   void cellClicked() {
      cellDelegate.onCellClicked(getModelObject());
   }

   @Override
   public void prepareForReuse() {
   }
}
