package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.api.GetDtlPlacesQuery;
import com.worldventures.dreamtrips.modules.dtl.event.PlacesUpdatedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlPlacesListFragment;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class DtlPlacesTabsPresenter extends Presenter<DtlPlacesTabsPresenter.View> {

    @Inject
    SnappyRepository db;

    private DtlLocation location;
    private List<DtlPlaceType> dtlPlaceTypes;

    public DtlPlacesTabsPresenter(@Nullable DtlLocation location) {
        if (location == null) {
            location = db.getSelectedDtlLocation();
        }
        this.location = location;
        dtlPlaceTypes = Arrays.asList(DtlPlaceType.OFFER, DtlPlaceType.DINING);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        setTabs();
        loadPlaces();
    }

    private void loadPlaces() {
        doRequest(new GetDtlPlacesQuery(location.getId()),
                dtlPlaces -> {
                    db.saveDtlPlaces(Queryable
                            .from(dtlPlaces)
                            .filter(element -> element.getType().equals(DtlPlaceType.DINING.getName()))
                            .toList());
                    eventBus.post(new PlacesUpdatedEvent(DtlPlaceType.DINING));
                    db.saveDtlPlaces(Queryable
                            .from(dtlPlaces)
                            .filter(element -> element.getType().equals(DtlPlaceType.OFFER.getName()))
                            .toList());
                    eventBus.post(new PlacesUpdatedEvent(DtlPlaceType.OFFER));
                },
                spiceException -> {
                    super.handleError(spiceException);
                    eventBus.post(new PlacesUpdatedEvent(true));
                }
        );
    }

    public void setTabs() {
        view.setTypes(dtlPlaceTypes);
        view.updateSelection();
    }

    public Bundle prepareArgsForTab(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DtlPlacesListFragment.EXTRA_TYPE, dtlPlaceTypes.get(position));
        return bundle;
    }

    public interface View extends Presenter.View {

        void setTypes(List<DtlPlaceType> types);

        void updateSelection();
    }
}
