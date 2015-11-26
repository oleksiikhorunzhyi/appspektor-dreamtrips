package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlacesFilterAttribute;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_filter_checkbox)
public class DtlFilterAttributeCell extends AbstractCell<DtlPlacesFilterAttribute> {

    @InjectView(R.id.textViewAttributeCaption)
    protected TextView textViewName;
    @InjectView(R.id.checkBox)
    protected CheckBox checkBox;

    public DtlFilterAttributeCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        textViewName.setText(getModelObject().getAttributeName());
        checkBox.setChecked(getModelObject().isChecked());
    }

    @OnClick(R.id.checkBox)
    void checkBoxClick() {
        getModelObject().setChecked(checkBox.isChecked());
    }

    @OnClick(R.id.textViewAttributeCaption)
    void textViewRegionClick() {
        checkBox.setChecked(!checkBox.isChecked());
        getModelObject().setChecked(checkBox.isChecked());
    }

    @Override
    public void prepareForReuse() {
        textViewName.setText("");
    }
}
