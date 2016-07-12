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
    @InjectView(R.id.trip_mark)
    TextView mark;

    public TripMapCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        int coverSize = itemView.getResources().getDimensionPixelSize(R.dimen.map_trip_detail_cover_size);
        cover.setImageURI(Uri.parse(getModelObject().getThumb(coverSize, coverSize)));
        title.setText(getModelObject().getName());
        location.setText(getModelObject().getGeoLocation().getName());
        date.setText(getModelObject().isHasMultipleDates()
                ? String.format(date.getResources().getString(R.string.multiple_dates), getModelObject().getAvailabilityDates().getStartDateString())
                : getModelObject().getAvailabilityDates().toString());
        price.setText(getModelObject().getPrice().toString());
        if (getModelObject().isFeatured()) {
            mark.setBackgroundColor(itemView.getResources().getColor(R.color.bucket_blue));
            mark.setText(R.string.featured_trip);
            mark.setVisibility(View.VISIBLE);
        } else if (getModelObject().isSoldOut()) {
            mark.setBackgroundColor(itemView.getResources().getColor(R.color.bucket_red));
            mark.setText(R.string.sold_out_trip);
            mark.setVisibility(View.VISIBLE);
        } else {
            mark.setVisibility(View.GONE);
        }
        //
        itemView.setOnClickListener(view -> cellDelegate.onCellClicked(getModelObject()));
    }

    @Override
    public void prepareForReuse() {

    }
}
