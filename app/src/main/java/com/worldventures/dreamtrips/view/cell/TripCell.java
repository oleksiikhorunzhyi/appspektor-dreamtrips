package com.worldventures.dreamtrips.view.cell;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.techery.spares.annotations.Layout;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Edward on 19.01.15.
 * cell for dream trips fragment
 */
@Layout(R.layout.adapter_item_trip)
public class TripCell extends AbstractCell<Trip> {

    @InjectView(R.id.imageViewTripImage)
    ImageView imageViewTripImage;
    @InjectView(R.id.imageViewLike)
    ImageView imageViewLike;
    @InjectView(R.id.textViewName)
    TextView textViewName;
    @InjectView(R.id.textViewPlace)
    TextView textViewPlace;
    @InjectView(R.id.textViewPrice)
    TextView textViewPrice;
    @InjectView(R.id.textViewDate)
    TextView textViewDate;

    @Inject
    UniversalImageLoader universalImageLoader;

    public TripCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        textViewName.setText(getModelObject().getName());
        textViewPlace.setText(getModelObject().getGeoLocation().getName());
        textViewPrice.setText(getModelObject().getPrice().toString());
        textViewDate.setText(getModelObject().getAvailabilityDates().toString());
        //TODO return original url
        universalImageLoader.loadImage("http://miriadna.com/desctopwalls/images/max/Green-nature.jpg",
                this.imageViewTripImage,
                null, new SimpleImageLoadingListener());
    }

    @OnClick(R.id.imageViewLike)
    void actionLike() {
        imageViewLike.setImageResource(R.drawable.ic_heart_2_sh);
    }

    @Override
    public void prepareForReuse() {
        textViewName.setText("");
        textViewPlace.setText("");
        textViewPrice.setText("");
    }
}
