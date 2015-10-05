package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.event.LocationClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_dtl_location)
public class DtlLocationCell extends AbstractCell<DtlLocation> {

    @InjectView(R.id.city)
    TextView city;
    @InjectView(R.id.state)
    TextView state;

    public DtlLocationCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        city.setText(getModelObject().getName());
        state.setText(getModelObject().getCountryName());
    }

    @OnClick(R.id.dtlLocationCellRoot) void cellClicked() {
        getEventBus().post(new LocationClickedEvent(getModelObject()));
    }

    @Override
    public void prepareForReuse() {
        //
    }
}
