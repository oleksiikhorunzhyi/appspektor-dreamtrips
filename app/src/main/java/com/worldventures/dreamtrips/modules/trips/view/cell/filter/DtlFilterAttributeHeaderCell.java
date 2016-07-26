package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.selectable.SelectableCell;
import com.worldventures.dreamtrips.core.selectable.SelectableDelegate;
import com.worldventures.dreamtrips.modules.common.view.adapter.item.SelectableHeaderItem;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_filter_header)
public class DtlFilterAttributeHeaderCell extends AbstractDelegateCell<SelectableHeaderItem,
        CellDelegate<SelectableHeaderItem>> implements SelectableCell {

    private SelectableDelegate selectableDelegate;

    @InjectView(R.id.checkBoxSelectAll) CheckBox checkBoxSelectAll;
    @InjectView(R.id.textViewAttributeHeaderCaption) TextView textViewHeaderCaption;

    public DtlFilterAttributeHeaderCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        textViewHeaderCaption.setText(getModelObject().getHeaderCaption());
        checkBoxSelectAll.setChecked(selectableDelegate.isSelected(getAdapterPosition()));
    }

    @OnClick(R.id.checkBoxSelectAll)
    void checkBoxClicked() {
        getModelObject().setSelected(checkBoxSelectAll.isChecked());
        selectableDelegate.setSelection(getAdapterPosition(), checkBoxSelectAll.isChecked());
        cellDelegate.onCellClicked(getModelObject());
    }

    @OnClick(R.id.textViewSelectAllCaption)
    void checkBoxTextViewClicked() {
        checkBoxSelectAll.setChecked(!checkBoxSelectAll.isChecked());
        checkBoxClicked();
    }

    @OnClick(R.id.textViewAttributeHeaderCaption)
    void headerCaptionClicked() {
    }

    @Override
    public void setSelectableDelegate(SelectableDelegate selectableDelegate) {
        this.selectableDelegate = selectableDelegate;
    }
}

