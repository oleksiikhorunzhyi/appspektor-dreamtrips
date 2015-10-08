package com.worldventures.dreamtrips.modules.dtl.presenter;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.event.PlacesUpdatedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;

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
        view.getAdapter().setItems(db.getDtlPlaces(placeType));
        if (view.getAdapter().getCount() < 1) view.showProgress();
    }

    public void onEventMainThread(PlacesUpdatedEvent event) {
        if (event.getType().equals(placeType)) {
            view.getAdapter().setItems(db.getDtlPlaces(placeType));
            // TODO : check if empty - show empty view
        }
        view.hideProgress();
    }

    public interface View extends Presenter.View {

        BaseArrayListAdapter<DtlPlace> getAdapter();

        void showProgress();

        void hideProgress();
    }
}
