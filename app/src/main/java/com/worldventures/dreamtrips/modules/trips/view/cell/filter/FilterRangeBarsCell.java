package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;

import com.appyvet.rangebar.RangeBar;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.events.RangeBarDurationEvent;
import com.worldventures.dreamtrips.core.utils.events.RangeBarPriceEvent;
import com.worldventures.dreamtrips.core.utils.events.ToggleRegionVisibilityEvent;
import com.worldventures.dreamtrips.modules.trips.model.FilterModel;

import butterknife.InjectView;
import butterknife.OnClick;


@Layout(R.layout.adapter_item_filters)
public class FilterRangeBarsCell extends AbstractCell<FilterModel> {

    @InjectView(R.id.rangeBarDay)
    protected RangeBar rangeBarDay;
    @InjectView(R.id.rangeBarPrice)
    protected RangeBar rangeBarPrice;

    private double maxPrice = Double.MAX_VALUE;
    private double minPrice = 0.0d;
    private int maxNights = Integer.MAX_VALUE;
    private int minNights = 0;

    public FilterRangeBarsCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        rangeBarDay.setTickSkipDrawEvery(3);
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
        this.rangeBarDay.setRangePinsByIndices(getModelObject().getIndexLeftDuration(), getModelObject().getIndexRightDuration());
        this.rangeBarPrice.setRangePinsByIndices(getModelObject().getIndexLeftPrice(), getModelObject().getIndexRightPrice());
    }

    @Override
    public void prepareForReuse() {
        //nothing to do here
    }
}
