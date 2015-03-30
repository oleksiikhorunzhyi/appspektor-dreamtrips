package com.worldventures.dreamtrips.modules.trips.view.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.presenter.FragmentMapInfoPresenter;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_trip_pin)
public class FragmentMapTripInfo extends BaseFragment<FragmentMapInfoPresenter> implements FragmentMapInfoPresenter.View {

    public static final String EXTRA_TRIP = "EXTRA_TRIP";

    @InjectView(R.id.imageViewTripImage)
    protected ImageView imageViewTripImage;
    @InjectView(R.id.imageViewLike)
    protected ImageView imageViewLike;
    @InjectView(R.id.textViewName)
    protected TextView textViewName;
    @InjectView(R.id.textViewPlace)
    protected TextView textViewPlace;
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

    @Inject
    protected UniversalImageLoader universalImageLoader;

    @Override
    public void afterCreateView(final View rootView) {
        super.afterCreateView(rootView);
        getPresenter().setTrip((TripModel) getArguments().getSerializable(EXTRA_TRIP));
        ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int fragmentHeight = itemLayout.getHeight();

                if (ViewUtils.isLandscapeOrientation(getActivity())) {
                    int offset = fragmentHeight / 2;
                    offset += getResources().getDimensionPixelSize(R.dimen.pin_offset);
                    getPresenter().sendOffset(offset);
                } else {
                    int centerY = rootView.getHeight() / 2;
                    int resultY = fragmentHeight + getResources().getDimensionPixelSize(R.dimen.pin_offset);
                    int offset = resultY - centerY;
                    getPresenter().sendOffset(offset);
                }

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
    public void setDate(String date) {
        textViewDate.setText(date);
    }

    @Override
    public void setDescription(String description) {
        textViewDescription.setText(description);
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
    public void setPointsInvisible() {
        pointsCountLayout.setVisibility(View.GONE);
    }

    @Override
    public void setFeatured(boolean isFeatured) {
        if (isFeatured) {
            textViewFeatured.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setPlace(String place) {
        textViewPlace.setText(place);
    }

    @Override
    public void setLiked(boolean liked) {
        imageViewLike.setImageResource(!liked ? R.drawable.ic_heart_1 : R.drawable.ic_bucket_like_selected);
    }

    @Override
    public void showLayout() {
        itemLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.itemLayout)
    void onClick() {
        getPresenter().onClick();
    }

    @Override
    protected FragmentMapInfoPresenter createPresenter(Bundle savedInstanceState) {
        return new FragmentMapInfoPresenter(this);
    }
}
