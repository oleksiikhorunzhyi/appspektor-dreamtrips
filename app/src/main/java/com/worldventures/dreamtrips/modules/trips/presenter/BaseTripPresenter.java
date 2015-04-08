package com.worldventures.dreamtrips.modules.trips.presenter;

import com.google.gson.JsonObject;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.worldventures.dreamtrips.R;
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

    public BaseTripPresenter(V view) {
        super(view);
    }

    public void setTrip(TripModel trip) {
        this.trip = trip;
    }

    public TripModel getTrip() {
        return trip;
    }

    public void actionLike() {
        trip.setLiked(!trip.isLiked());
        view.setLike(trip.isLiked());

        RequestListener<JsonObject> callback = new RequestListener<JsonObject>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                trip.setLiked(!trip.isLiked());
                view.informUser(R.string.smth_went_wrong);
            }

            @Override
            public void onRequestSuccess(JsonObject jsonObject) {
                eventBus.post(new TripLikedEvent(trip));
                db.saveTrip(trip);
            }
        };

        if (trip.isLiked()) {
            dreamSpiceManager.execute(new LikeTripCommand(trip.getId()), callback);
        } else {
            dreamSpiceManager.execute(new UnlikeTripCommand(trip.getId()), callback);
        }
    }


    @Override
    public void resume() {
        super.resume();
        view.setName(trip.getName());
        view.setDates(trip.getAvailabilityDates().toString());
        view.setDesription(trip.getDescription());
        view.setLocation(trip.getGeoLocation().getName());
        view.setPrice(trip.getPrice().toString());
        view.setDuration(trip.getDuration());
        view.setLike(trip.isLiked());

        if (trip.getRewardsLimit() > 0) {
            view.setRedemption(String.valueOf(trip.getRewardsLimit()));
        } else {
            view.setPointsInvisible();
        }

        if (trip.isFeatured()) {
            view.setFeatured();
        }

        view.setLike(trip.isLiked());
    }

    public static interface View extends Presenter.View {
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
