package com.worldventures.dreamtrips.modules.trips.view.cell;

import android.graphics.PointF;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.techery.spares.annotations.Layout;
import com.techery.spares.session.SessionHolder;
import com.techery.spares.ui.view.cell.AbstractCell;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.utils.events.AddToBucketEvent;
import com.worldventures.dreamtrips.core.utils.events.LikeTripPressedEvent;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.feed.bundle.FeedItemDetailsBundle;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;
import com.worldventures.dreamtrips.modules.trips.event.TripItemAnalyticEvent;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.adapter_item_trip)
public class TripCell extends AbstractCell<TripModel> {

    @InjectView(R.id.imageViewTripImage)
    protected SimpleDraweeView imageViewTripImage;
    @InjectView(R.id.imageViewLike)
    protected CheckedTextView likeView;
    @InjectView(R.id.imageViewAddToBucket)
    protected CheckedTextView addToBucketView;
    @InjectView(R.id.textViewName)
    protected TextView textViewName;
    @InjectView(R.id.textViewPlace)
    protected TextView textViewPlace;
    @InjectView(R.id.textViewPrice)
    protected TextView textViewPrice;
    @InjectView(R.id.textViewDate)
    protected TextView textViewDate;
    @InjectView(R.id.sold_out)
    protected ImageView soldOut;
    @InjectView(R.id.textViewPoints)
    protected TextView textViewPoints;
    @InjectView(R.id.pointsCountLayout)
    protected FrameLayout pointsCountLayout;
    @InjectView(R.id.textViewFeatured)
    protected TextView textViewFeatured;

    @Inject
    protected SessionHolder<UserSession> appSessionHolder;
    @Inject
    protected Presenter.TabletAnalytic tabletAnalytic;
    @Inject
    ActivityRouter activityRouter;

    public TripCell(View view) {
        super(view);
    }

    @Override
    protected void syncUIStateWithModel() {
        textViewName.setText(getModelObject().getName());
        textViewPlace.setText(getModelObject().getGeoLocation().getName());
        textViewPrice.setText(getModelObject().getPrice().toString());
        if (getModelObject().isHasMultipleDates()) {
            textViewDate.setText(String.format(itemView.getContext().getResources().getString(R.string.multiple_dates),
                    getModelObject().getAvailabilityDates().getStartDateString()));
        } else {
            textViewDate.setText(getModelObject().getAvailabilityDates().toString());
        }

        if (getModelObject().isFeatured()) {
            textViewFeatured.setVisibility(View.VISIBLE);
        } else {
            textViewFeatured.setVisibility(View.GONE);
        }

        if (getModelObject().isSoldOut()) {
            soldOut.setVisibility(View.VISIBLE);
        } else {
            soldOut.setVisibility(View.GONE);
        }

        String reward = getModelObject().getRewardsLimit(appSessionHolder.get().get().getUser());

        if (!TextUtils.isEmpty(reward) && !"0".equals(reward)) {
            textViewPoints.setText(String.valueOf(reward));
            pointsCountLayout.setVisibility(View.VISIBLE);
        } else {
            pointsCountLayout.setVisibility(View.GONE);
        }

        likeView.setChecked(getModelObject().isLiked());
        addToBucketView.setChecked(getModelObject().isInBucketList());
        addToBucketView.setEnabled(!getModelObject().isInBucketList());

        PointF pointF = new PointF(0.5f, 0.0f);
        imageViewTripImage.getHierarchy().setActualImageFocusPoint(pointF);
        imageViewTripImage.setImageURI(Uri.parse(getModelObject().getThumb(itemView.getResources())));
    }

    @OnClick(R.id.imageViewLike)
    void onLike() {
        TripModel tripModel = getModelObject();
        syncUIStateWithModel();
        getEventBus().post(new LikeTripPressedEvent(getModelObject()));
        getEventBus().post(new TripItemAnalyticEvent(TrackingHelper.ATTRIBUTE_LIKE, tripModel.getTripId()));
    }

    @OnClick(R.id.imageViewAddToBucket)
    void onAddToBucket() {
        TripModel tripModel = getModelObject();
        getModelObject().setInBucketList(true);
        syncUIStateWithModel();
        getEventBus().post(new AddToBucketEvent(tripModel));
        getEventBus().post(new TripItemAnalyticEvent(TrackingHelper.ATTRIBUTE_ADD_TO_BUCKET_LIST, tripModel.getTripId()));
    }

    @OnClick(R.id.itemLayout)
    public void actionItemClick() {
        Route detailsRoute = Route.FEED_ITEM_DETAILS;
        FeedItemDetailsBundle bundle = new FeedItemDetailsBundle(FeedItem.create(getModelObject(),
                appSessionHolder.get().get().getUser()));
        if (tabletAnalytic.isTabletLandscape()) {
            bundle.setSlave(true);
        }
        NavigationBuilder.create().with(activityRouter).data(bundle).move(detailsRoute);


        getEventBus().post(new TripItemAnalyticEvent(TrackingHelper.ATTRIBUTE_VIEW, getModelObject().getTripId()));
    }

    @OnClick(R.id.layoutInfo)
    void onInfoClick() {
        actionItemClick();
    }

    @Override
    public void prepareForReuse() {
    }

}
