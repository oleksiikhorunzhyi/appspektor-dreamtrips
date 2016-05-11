package com.worldventures.dreamtrips.modules.trips.presenter;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;

import java.util.List;

public class TripMapListPresenter extends Presenter<TripMapListPresenter.View> {

    private List<TripModel> trips;

    public TripMapListPresenter(List<TripModel> trips) {
        this.trips = trips;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        //
        view.updateItems(trips);
    }

    public interface View extends Presenter.View {

        void updateItems(List<TripModel> trips);
    }
}
