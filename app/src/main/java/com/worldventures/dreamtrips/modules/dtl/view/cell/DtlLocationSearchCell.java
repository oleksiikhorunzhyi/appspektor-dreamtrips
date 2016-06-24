package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;
import android.widget.TextView;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.dtl.analytics.DtlAnalyticsCommand;
import com.worldventures.dreamtrips.modules.dtl.analytics.LocationSearchEvent;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlExternalLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocationType;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_dtl_location_search)
public class DtlLocationSearchCell extends AbstractDelegateCell<DtlExternalLocation, CellDelegate<DtlExternalLocation>> {

    @InjectView(R.id.locationName)
    TextView locationName;

    @Inject
    AnalyticsInteractor analyticsInteractor;

    public DtlLocationSearchCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        StringBuilder sb = new StringBuilder();
        sb.append(getModelObject().getLongName());
        Queryable.from(getModelObject().getLocatedIn())
                .filter(temp -> (temp.getType() != DtlLocationType.METRO))
                .sort(DtlExternalLocation.CATEGORY_COMPARATOR)
                .forEachR(tempLocation -> {
                    sb.append(", ");
                    sb.append(tempLocation.getLongName());
                });
        locationName.setText(sb.toString());
    }

    @OnClick(R.id.locationName)
    void cellClicked() {
        analyticsInteractor.dtlAnalyticsCommandPipe()
                .send(DtlAnalyticsCommand.create(new LocationSearchEvent(getModelObject())));
        cellDelegate.onCellClicked(getModelObject());
    }

    @Override
    public void prepareForReuse() {
    }
}
