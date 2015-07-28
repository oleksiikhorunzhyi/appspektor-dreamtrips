package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.presenter.TripMapInfoPresenter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_trip_pin)
public class TripMapInfoFragment extends BaseFragment<TripMapInfoPresenter> implements TripMapInfoPresenter.View {

    public static final String EXTRA_TRIP = "EXTRA_TRIP";

    @InjectView(R.id.imageViewTripImage)
    protected SimpleDraweeView imageViewTripImage;
    @InjectView(R.id.imageViewAddToBucket)
    protected CheckedTextView addToBucketView;
    @InjectView(R.id.imageViewLike)
    protected CheckedTextView likeView;
    @InjectView(R.id.textViewName)
    protected TextView textViewName;
    @InjectView(R.id.textViewPlace)
    protected TextView textViewPlace;
    @InjectView(R.id.sold_out)
    protected ImageView soldOut;
    @InjectView(R.id.textViewPrice)
    protected TextView textViewPrice;
    @InjectView(R.id.textViewDate)
    protected TextView textViewDate;
    @InjectView(R.id.textViewPoints)
    protected TextView textViewPoints;
    @InjectView(R.id.textViewDescription)
    protected TextView textViewDescription;
    @InjectView(R.id.itemLayout)
    protected RelativeLayout itemLayout;
    @InjectView(R.id.pointsCountLayout)
    protected FrameLayout pointsCountLayout;
    @InjectView(R.id.textViewFeatured)
    protected TextView textViewFeatured;

    @Override
    protected TripMapInfoPresenter createPresenter(Bundle savedInstanceState) {
        return new TripMapInfoPresenter();
    }

    @Override
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void afterCreateView(final View rootView) {
        super.afterCreateView(rootView);
        getPresenter().setTrip((TripModel) getArguments().getSerializable(EXTRA_TRIP));
        ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int fragmentHeight = itemLayout.getHeight();
                int offset;
                if (ViewUtils.isLandscapeOrientation(getActivity())) {
                    offset = fragmentHeight / 2;
                    offset += getResources().getDimensionPixelSize(R.dimen.spacing_huge);
                } else {
                    int centerY = rootView.getHeight() / 2;
                    int resultY = fragmentHeight + getResources().getDimensionPixelSize(R.dimen.spacing_huge);
                    offset = resultY - centerY;

                }
                getPresenter().sendOffset(offset);
                ViewTreeObserver obs = rootView.getViewTreeObserver();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override
    public void setName(String name) {
        textViewName.setText(name);
    }

    @Override
    public void setDates(String date) {
        textViewDate.setText(date);
    }

    @Override
    public void setDesription(String text) {
        textViewDescription.setText(text);
    }

    @Override
    public void setImage(String image) {
        imageViewTripImage.setImageURI(Uri.parse(image));
    }

    @Override
    public void setPrice(String price) {
        textViewPrice.setText(price);
    }

    @Override
    public void setRedemption(String points) {
        textViewPoints.setText(points);
    }

    @Override
    public void setPointsInvisible() {
        pointsCountLayout.setVisibility(View.GONE);
    }

    @Override
    public void setFeatured() {
        textViewFeatured.setVisibility(View.VISIBLE);
    }

    @Override
    public void setLocation(String place) {
        textViewPlace.setText(place);
    }

    @Override
    public void setSoldOut() {
        soldOut.setVisibility(View.VISIBLE);
    }

    @Override
    public void setLike(boolean liked) {
        likeView.setChecked(liked);
    }

    @Override
    public void setInBucket(boolean inBucket) {
        addToBucketView.setChecked(inBucket);
        addToBucketView.setEnabled(!inBucket);
    }

    @Override
    public void showLayout() {
        itemLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.itemLayout)
    void onClick() {
        getPresenter().onClick();
    }

    @OnClick(R.id.imageViewAddToBucket)
    void onAddToBucket() {
        getPresenter().addTripToBucket();
    }

    @OnClick(R.id.imageViewLike)
    void onLike() {
        getPresenter().likeTrip();
    }

    @Override
    public void setDuration(int count) {
        //nothing to do here
    }

}
