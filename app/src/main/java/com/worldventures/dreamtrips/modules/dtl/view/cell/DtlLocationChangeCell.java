package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.ui.annotations.Layout;
import com.worldventures.core.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.locations.model.LocationType;
import com.worldventures.dreamtrips.modules.common.view.adapter.BaseAbstractDelegateCell;
import com.worldventures.dreamtrips.modules.dtl.helper.comparator.LocationComparator;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_dtl_location_change)
public class DtlLocationChangeCell extends BaseAbstractDelegateCell<DtlLocation, CellDelegate<DtlLocation>> {

   @InjectView(R.id.locationName) TextView locationName;

   public DtlLocationChangeCell(View view) {
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
      locationName.setCompoundDrawablesWithIntrinsicBounds(getModelObject().type() == LocationType.CITY ? R.drawable.city_icon : R.drawable.metro_area_icon, 0, 0, 0);
   }

   @OnClick(R.id.dtlLocationCellRoot)
   void cellClicked() {
      cellDelegate.onCellClicked(getModelObject());
   }

   @Override
   public void prepareForReuse() {
   }
}
