package com.worldventures.dreamtrips.modules.trips.view.cell;

import android.view.View;
import android.widget.CheckBox;

import com.appyvet.rangebar.RangeBar;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.model.FilterModel;
import com.worldventures.dreamtrips.core.utils.events.CheckBoxAllPressedEvent;
import com.worldventures.dreamtrips.core.utils.events.RangeBarDurationEvent;
import com.worldventures.dreamtrips.core.utils.events.RangeBarPriceEvent;
import com.worldventures.dreamtrips.core.utils.events.ToggleRegionVisibilityEvent;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Edward on 22.01.15.
 */
@Layout(R.layout.adapter_item_filters)
public class FiltersCell extends AbstractCell<FilterModel> {

    @InjectView(R.id.rangeBarDay)
    RangeBar rangeBarDay;
    @InjectView(R.id.rangeBarPrice)
    RangeBar rangeBarPrice;
    @InjectView(R.id.checkBoxSelectAll)
    CheckBox checkBoxSelectAll;

    private double maxPrice = Double.MAX_VALUE;
    private double minPrice = 0.0d;
    private int maxNights = Integer.MAX_VALUE;
    private int minNights = 0;

    public FiltersCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        this.rangeBarDay.setRangePinsByIndices(getModelObject().getIndexLeftDuration(), getModelObject().getIndexRightDuration());
        this.rangeBarPrice.setRangePinsByIndices(getModelObject().getIndexLeftPrice(), getModelObject().getIndexRightPrice());
        this.rangeBarDay.setOnRangeBarChangeListener((rangeBar, i, i2, s, s2) -> {
            minNights = Integer.valueOf(s);
            maxNights = i2 == (rangeBarDay.getTickCount() - 1) ? Integer.MAX_VALUE : Integer.valueOf(s2);
            getModelObject().setIndexLeftDuration(i);
            getModelObject().setIndexRightDuration(i2);
            getEventBus().post(new RangeBarDurationEvent(minNights, maxNights));
        });
        this.rangeBarPrice.setOnRangeBarChangeListener((rangeBar, i, i2, s, s2) -> {
            minPrice = Double.valueOf(s);
            maxPrice = i2 == (rangeBarPrice.getTickCount() - 1) ? Double.MAX_VALUE : Double.valueOf(s2);
            getModelObject().setIndexLeftPrice(i);
            getModelObject().setIndexRightPrice(i2);
            getEventBus().post(new RangeBarPriceEvent(minPrice, maxPrice));
        });
        checkBoxSelectAll.setChecked(getModelObject().isChecked());

    }

    @OnClick(R.id.checkBoxSelectAll)
    void checkBoxClicked() {
        getModelObject().setChecked(checkBoxSelectAll.isChecked());
        getEventBus().post(new CheckBoxAllPressedEvent(checkBoxSelectAll.isChecked()));
    }

    @OnClick(R.id.listHeader)
    void toggleVisibility() {
        getEventBus().post(new ToggleRegionVisibilityEvent());
    }

    @OnClick(R.id.textViewSelectAll)
    void checkBoxTextViewClicked() {
        checkBoxSelectAll.setChecked(!checkBoxSelectAll.isChecked());
        getModelObject().setChecked(checkBoxSelectAll.isChecked());
        getEventBus().post(new CheckBoxAllPressedEvent(checkBoxSelectAll.isChecked()));
    }

    @Override
    public void prepareForReuse() {

    }
}
