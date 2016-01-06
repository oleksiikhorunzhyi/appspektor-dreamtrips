package com.worldventures.dreamtrips.modules.dtl.view.cell;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.selectable.SelectableCell;
import com.worldventures.dreamtrips.core.selectable.SelectableDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.cell_distance)
public class DtlDistanceCell extends AbstractDelegateCell<DtlFilterData.DistanceType,
        CellDelegate<DtlFilterData.DistanceType>> implements SelectableCell {

    @InjectView(R.id.distance)
    TextView distance;
    @InjectView(R.id.selection_toggle)
    ImageView selectionToggle;

    private SelectableDelegate selectableDelegate;

    public DtlDistanceCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        selectionToggle.setVisibility(selectableDelegate.isSelected(getAdapterPosition())
                ? View.VISIBLE : View.GONE);
        //
        switch (getModelObject()) {
            case MILES:
                distance.setText(R.string.distance_dialog_miles);
                break;
            case KMS:
                distance.setText(R.string.distance_dialog_kms);
                break;
        }
    }

    @OnClick(R.id.distance_holder)
    public void onDistanceClicked() {
        selectableDelegate.toggleSelection(getAdapterPosition());
        cellDelegate.onCellClicked(getModelObject());
    }

    @Override
    public void prepareForReuse() {

    }

    @Override
    public void setSelectableDelegate(SelectableDelegate selectableDelegate) {
        this.selectableDelegate = selectableDelegate;
    }

}
