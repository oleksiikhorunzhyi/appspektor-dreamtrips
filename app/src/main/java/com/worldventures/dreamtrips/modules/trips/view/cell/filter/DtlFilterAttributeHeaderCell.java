package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.event.FilterAttributesSelectAllEvent;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlPlacesFilterAttribute;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_filter_header)
public class DtlFilterAttributeHeaderCell extends AbstractCell<DtlPlacesFilterAttribute> {

    @InjectView(R.id.checkBoxSelectAll)
    protected CheckBox checkBoxSelectAll;
    @InjectView(R.id.textViewAttributeHeaderCaption)
    protected TextView textViewHeaderCaption;

    public DtlFilterAttributeHeaderCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        textViewHeaderCaption.setText(getModelObject().getAttributeName());
        checkBoxSelectAll.setChecked(getModelObject().isChecked());
    }

    @OnClick(R.id.checkBoxSelectAll)
    void checkBoxClicked() {
        getModelObject().setChecked(checkBoxSelectAll.isChecked());
        getEventBus().post(new FilterAttributesSelectAllEvent(checkBoxSelectAll.isChecked()));
    }

    @OnClick(R.id.textViewSelectAllCaption)
    void checkBoxTextViewClicked() {
        checkBoxSelectAll.setChecked(!checkBoxSelectAll.isChecked());
        checkBoxClicked();
    }

    @OnClick(R.id.textViewAttributeHeaderCaption)
    void headerCaptionClicked() { /* */ }

    @Override
    public void prepareForReuse() {
        //nothing to do here
    }
}

