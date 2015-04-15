package com.worldventures.dreamtrips.modules.trips.view.cell;

import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.UniversalImageLoader;
import com.worldventures.dreamtrips.core.utils.events.LikeTripEvent;
import com.worldventures.dreamtrips.core.utils.events.TouchTripEvent;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_trip)
public class TripCell extends AbstractCell<TripModel> {

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
    @InjectView(R.id.pointsCountLayout)
    protected FrameLayout pointsCountLayout;
    @InjectView(R.id.textViewFeatured)
    protected TextView textViewFeatured;

    @Inject
    protected SessionHolder<UserSession> appSessionHolder;


    @Inject
    protected UniversalImageLoader universalImageLoader;

    public TripCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        textViewName.setText(getModelObject().getName());
        textViewPlace.setText(getModelObject().getGeoLocation().getName());
        textViewPrice.setText(getModelObject().getPrice().toString());
        textViewDate.setText(getModelObject().getAvailabilityDates().toString());

        if (getModelObject().isFeatured()) {
            textViewFeatured.setVisibility(View.VISIBLE);
        } else {
            textViewFeatured.setVisibility(View.GONE);
        }

        String reward = getModelObject().getRewardsLimit((appSessionHolder.get().get().getUser()));

        if (!TextUtils.isEmpty(reward)) {
            textViewPoints.setText(String.valueOf(reward));
            pointsCountLayout.setVisibility(View.VISIBLE);
        } else {
            pointsCountLayout.setVisibility(View.GONE);
        }

        setImageViewLike();
        universalImageLoader.loadImage(getModelObject().getImageUrl("THUMB"),
                this.imageViewTripImage,
                UniversalImageLoader.OP_LIST_SCREEN, new SimpleImageLoadingListener());
    }

    @OnClick(R.id.imageViewLike)
    void actionLike() {
        getModelObject().setLiked(!getModelObject().isLiked());
        setImageViewLike();
        getEventBus().post(new LikeTripEvent(getModelObject()));
    }

    @OnClick(R.id.itemLayout)
    void actionItemClick() {
        getEventBus().post(new TouchTripEvent(getModelObject()));
    }

    @OnClick(R.id.layoutInfo)
    void onInfoClick() {
        actionItemClick();
    }

    private void setImageViewLike() {
        if (getModelObject().isLiked()) {
            imageViewLike.setImageResource(R.drawable.ic_bucket_like_selected);
        } else {
            imageViewLike.setImageResource(R.drawable.ic_heart_1);
        }
    }

    @Override
    public void prepareForReuse() {
        textViewName.setText("");
        textViewPlace.setText("");
        textViewPrice.setText("");
        imageViewTripImage.setImageBitmap(null);
    }
}
