package com.worldventures.dreamtrips.modules.trips.view.cell.filter;

import android.view.View;

import com.appyvet.rangebar.RangeBar;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.model.FilterModel;

import butterknife.InjectView;
import timber.log.Timber;

@Layout(R.layout.adapter_item_filters)
public class FilterRangeBarsCell extends AbstractDelegateCell<FilterModel, FilterRangeBarsCell.Delegate> {

    @InjectView(R.id.rangeBarDay) RangeBar rangeBarDay;
    @InjectView(R.id.rangeBarPrice) RangeBar rangeBarPrice;

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
        rangeBarDay.setOnRangeBarChangeListener((rangeBar, i, i2, s, s2) -> {
            minNights = Integer.valueOf(s);
            maxNights = i2 == (rangeBarDay.getTickCount() - 1) ? Integer.MAX_VALUE : Integer.valueOf(s2);
            getModelObject().setIndexLeftDuration(i);
            getModelObject().setIndexRightDuration(i2);
            cellDelegate.rangeBarDurationEvent(minNights, maxNights);
        });
        rangeBarPrice.setOnRangeBarChangeListener((rangeBar, i, i2, s, s2) -> {
            minPrice = Double.valueOf(s);
            maxPrice = i2 == (rangeBarPrice.getTickCount() - 1) ? Double.MAX_VALUE : Double.valueOf(s2);
            getModelObject().setIndexLeftPrice(i);
            getModelObject().setIndexRightPrice(i2);
            cellDelegate.rangeBarPriceEvent(minPrice, maxPrice);
        });

        //in case of rare bug like:
        //java.lang.IllegalArgumentException: Pin index left 0, or right 13 is out of bounds.
        //Check that it is greater than the minimum (100.0) and less than the maximum value (500.0)
        int leftDuration = getModelObject().getIndexLeftDuration();
        int rightDuration = getModelObject().getIndexRightDuration();
        if (leftDuration < minNights || rightDuration > maxNights) {
            rangeBarDay.setRangePinsByIndices(minNights, maxNights);
            Timber.e("left duration = %d; right duration = %d; not in range (%d %d)", leftDuration, rightDuration, minNights, maxNights);
        } else {
            rangeBarDay.setRangePinsByIndices(leftDuration, rightDuration);
        }

        int leftPrice = getModelObject().getIndexLeftPrice();
        int rightPrice = getModelObject().getIndexRightPrice();
        if (leftPrice < minPrice || rightPrice > maxPrice) {
            rangeBarPrice.setRangePinsByIndices((int)minPrice, (int)maxPrice);
            Timber.e("left price = %d; right price = %d; not in range (%d %d)", leftPrice, rightPrice, minPrice, maxPrice);
        } else {
            rangeBarPrice.setRangePinsByIndices(leftPrice, rightPrice);
        }
    }

    public interface Delegate extends CellDelegate<FilterModel> {

        void rangeBarDurationEvent(int minNights, int maxNights);

        void rangeBarPriceEvent(double minPrice, double maxPrice);
    }
}
