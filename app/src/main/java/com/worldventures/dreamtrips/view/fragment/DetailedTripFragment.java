package com.worldventures.dreamtrips.view.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.Trip;
import com.worldventures.dreamtrips.presentation.DetailedTripFragmentPM;
import com.worldventures.dreamtrips.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.view.activity.DetailTripActivity;
import com.worldventures.dreamtrips.view.activity.MainActivity;

import java.text.Format;

import javax.inject.Inject;

import butterknife.InjectView;

/**
 * Created by Edward on 19.01.15.
 * fragment to show detailed trip
 */
@Layout(R.layout.fragment_detailed_trip)
@MenuResource(R.menu.menu_detailed_trip)
public class DetailedTripFragment extends BaseFragment<DetailedTripFragmentPM> implements DetailedTripFragmentPM.View {

    @InjectView(R.id.imageViewTripImage)
    ImageView imageViewTripImage;
    @InjectView(R.id.progressBarImage)
    ProgressBar progressBarImage;
    @InjectView(R.id.textViewName)
    TextView textViewName;
    @InjectView(R.id.textViewPlace)
    TextView textViewPlace;
    @InjectView(R.id.textViewPrice)
    TextView textViewPrice;
    @InjectView(R.id.textViewDate)
    TextView textViewDate;
    @InjectView(R.id.textViewDescription)
    TextView textViewDescription;
    @InjectView(R.id.textViewScheduleDescription)
    TextView textViewScheduleDescription;

    @Inject
    UniversalImageLoader universalImageLoader;

    private Trip trip;

    @Override
    protected DetailedTripFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new DetailedTripFragmentPM(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trip = (Trip) getArguments().getSerializable(DetailTripActivity.EXTRA_TRIP);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem menuItem = menu.findItem(R.id.action_like);
        menuItem.setIcon(trip.isLiked() ? R.drawable.ic_heart_2_sh : R.drawable.ic_heart_1_sh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_like):
                item.setIcon(!trip.isLiked() ? R.drawable.ic_heart_2_sh : R.drawable.ic_heart_1_sh);
                trip.setLiked(!trip.isLiked());
                getPresentationModel().actionLike();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showErrorMessage() {
        ((DetailTripActivity) getActivity()).informUser(getString(R.string.smth_went_wrong));
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        getPresentationModel().setTrip(trip);
        getPresentationModel().onCreate();
    }

    @Override
    public void setDesription(String description) {
        textViewDescription.setText(description);
    }

    @Override
    public void setDates(String dates) {
        textViewDate.setText(dates);
    }

    @Override
    public void setPrice(String price) {
        textViewPrice.setText(price);
    }

    @Override
    public void setLocation(String location) {
        textViewPlace.setText(location);
    }

    @Override
    public void setName(String name) {
        textViewName.setText(name);
    }

    @Override
    public void loadPhoto(String url) {
        universalImageLoader.loadImage(trip.getImageUrl("NORMAL"),
                this.imageViewTripImage,
                null, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        progressBarImage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        progressBarImage.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void setDuration(int count) {
        textViewScheduleDescription.setText(String.format(getString(R.string.duration), count));
    }
}
