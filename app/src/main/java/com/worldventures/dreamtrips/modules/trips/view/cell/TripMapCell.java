package com.worldventures.dreamtrips.modules.trips.view.cell;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractDelegateCell;
import com.techery.spares.ui.view.cell.CellDelegate;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import butterknife.InjectView;

@Layout(R.layout.adapter_item_trip_map)
public class TripMapCell extends AbstractDelegateCell<TripModel, CellDelegate<TripModel>> {

    @InjectView(R.id.trip_cover)
    SimpleDraweeView cover;
    @InjectView(R.id.trip_title)
    TextView title;
    @InjectView(R.id.trip_location)
    TextView location;
    @InjectView(R.id.trip_date)
    TextView date;
    @InjectView(R.id.trip_price)
    TextView price;

    public TripMapCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        cover.setImageURI(Uri.parse(getModelObject().getThumb(itemView.getResources())));
        title.setText(getModelObject().getName());
        location.setText(getModelObject().getGeoLocation().getName());
        date.setText(getModelObject().isHasMultipleDates()
                ? String.format(date.getResources().getString(R.string.multiple_dates), getModelObject().getAvailabilityDates().getStartDateString())
                : getModelObject().getAvailabilityDates().toString());
        price.setText(getModelObject().getPrice().toString());
        //
        itemView.setOnClickListener(view -> cellDelegate.onCellClicked(getModelObject()));
    }

    @Override
    public void prepareForReuse() {

    }
}
