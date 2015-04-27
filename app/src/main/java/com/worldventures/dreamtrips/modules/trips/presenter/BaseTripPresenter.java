package com.worldventures.dreamtrips.modules.trips.presenter;

import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.events.TripLikedEvent;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.api.LikeTripCommand;
import com.worldventures.dreamtrips.modules.trips.api.UnlikeTripCommand;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import javax.inject.Inject;

public class BaseTripPresenter<V extends BaseTripPresenter.View> extends Presenter<V> {

    @Inject
    protected SnappyRepository db;

    protected TripModel trip;

    public TripModel getTrip() {
        return trip;
    }

    public void setTrip(TripModel trip) {
        this.trip = trip;
    }

    public void actionLike() {
        trip.setLiked(!trip.isLiked());
        view.setLike(trip.isLiked());

        DreamTripsRequest<JsonObject> request = trip.isLiked() ?
                new LikeTripCommand(trip.getLikeId()) :
                new UnlikeTripCommand(trip.getLikeId());

        doRequest(request, object -> onSuccess());
    }

    private void onSuccess() {
        eventBus.post(new TripLikedEvent(trip));
        db.saveTrip(trip);
    }

    @Override
    public void handleError(SpiceException error) {
        trip.setLiked(!trip.isLiked());
        super.handleError(error);
    }

    @Override
    public void onResume() {
        super.onResume();
        view.setName(trip.getName());
        view.setDates(trip.getAvailabilityDates().toString());
        view.setDesription(trip.getDescription());
        view.setLocation(trip.getGeoLocation().getName());
        view.setPrice(trip.getPrice().toString());
        view.setDuration(trip.getDuration());
        view.setLike(trip.isLiked());
        String reward = trip.getRewardsLimit(appSessionHolder.get().get().getUser());

        if (!TextUtils.isEmpty(reward) && !"0".equals(reward)) {
            view.setRedemption(String.valueOf(reward));
        } else {
            view.setPointsInvisible();
        }

        if (trip.isFeatured()) {
            view.setFeatured();
        }

        view.setLike(trip.isLiked());
    }

    public interface View extends Presenter.View {
        void setName(String text);

        void setLocation(String text);

        void setPrice(String text);

        void setDates(String text);

        void setDesription(String text);

        void setDuration(int count);

        void setRedemption(String count);

        void setLike(boolean like);
        void setPointsInvisible();

        void setFeatured();
    }

}
