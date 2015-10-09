package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.event.PlacesUpdateFinished;
import com.worldventures.dreamtrips.modules.dtl.event.PlacesUpdatedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;

import java.util.List;

import javax.inject.Inject;

public class DtlPlacesListPresenter extends Presenter<DtlPlacesListPresenter.View> {

    @Inject
    SnappyRepository db;

    protected DtlPlaceType placeType;

    public DtlPlacesListPresenter(DtlPlaceType placeType) {
        this.placeType = placeType;
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        List<DtlPlace> dtlPlaces = db.getDtlPlaces(placeType);
        view.setItems(dtlPlaces);
        if (dtlPlaces.isEmpty()) view.showProgress();
    }

    public void onEventMainThread(PlacesUpdatedEvent event) {
        if (!event.getType().equals(placeType)) return;
        //
        view.setItems(db.getDtlPlaces(placeType));
    }

    public void onEventMainThread(PlacesUpdateFinished event) {
        view.hideProgress();
    }

    public interface View extends Presenter.View {

        void setItems(List<DtlPlace> places);

        void showProgress();

        void hideProgress();
    }
}
