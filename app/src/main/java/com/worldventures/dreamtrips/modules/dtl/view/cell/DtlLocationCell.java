package com.worldventures.dreamtrips.modules.dtl.view.cell;


import android.view.View;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.TextUtils;
import com.worldventures.dreamtrips.modules.dtl.event.LocationClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_dtl_location)
public class DtlLocationCell extends AbstractCell<DtlLocation> {

    @InjectView(R.id.city_state)
    TextView city;

    public DtlLocationCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        city.setText(TextUtils.join(", ", getModelObject().getName(),
                getModelObject().getCountryName()));
    }

    @OnClick(R.id.dtlLocationCellRoot)
    void cellClicked() {
        getEventBus().post(new LocationClickedEvent(getModelObject()));
    }

    @Override
    public void prepareForReuse() {
        //
    }
}
