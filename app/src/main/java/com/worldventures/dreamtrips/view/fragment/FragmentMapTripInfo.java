package com.worldventures.dreamtrips.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.presentation.FragmentMapInfoPM;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * Created by 1 on 28.01.15.
 */
@Layout(R.layout.fragment_trip_pin)
public class FragmentMapTripInfo extends BaseFragment<FragmentMapInfoPM> implements FragmentMapInfoPM.View {

    public static final String EXTRA_TRIP = "EXTRA_TRIP";

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
    @InjectView(R.id.textViewPoints)
    TextView textViewPoints;

    @Inject
    UniversalImageLoader universalImageLoader;

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        getPresentationModel().setTrip((Trip) getArguments().getSerializable(EXTRA_TRIP));
    }

    @Override
    public void setName(String name) {
        textViewName.setText(name);
    }

    @Override
    public void setDate(String date) {
        textViewDate.setText(date);
    }

    @Override
    public void setImage(String image) {
        universalImageLoader.loadImage(image,
                this.imageViewTripImage,
                UniversalImageLoader.OP_LIST_SCREEN, new SimpleImageLoadingListener());
    }

    @Override
    public void setPrice(String price) {
        textViewPrice.setText(price);
    }

    @Override
    public void setPoints(String points) {
        textViewPoints.setText(points);
    }

    @Override
    public void setPlace(String place) {
        textViewPlace.setText(place);
    }

    @Override
    public void setLiked(boolean liked) {
        imageViewLike.setImageResource(liked ? R.drawable.ic_heart_2_sh : R.drawable.ic_heart_1_sh);
    }

    @Override
    protected FragmentMapInfoPM createPresentationModel(Bundle savedInstanceState) {
        return new FragmentMapInfoPM(this);
    }
}
