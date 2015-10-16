package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.api.GetDtlPlacesQuery;
import com.worldventures.dreamtrips.modules.dtl.event.PlacesUpdateFinished;
import com.worldventures.dreamtrips.modules.dtl.event.PlacesUpdatedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlPlacesListFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
        view.initToolbar(location);
        setTabs();
        loadPlaces();
    }

    private void loadPlaces() {
        doRequest(new GetDtlPlacesQuery(location.getId()),
                dtlPlaces -> {
                    Map<DtlPlaceType, Collection<DtlPlace>> byType =
                            Queryable.from(dtlPlaces).groupToMap(DtlPlace::getType);
                    for (DtlPlaceType type : byType.keySet()) {
                        updatePlacesByType(type, byType.get(type));
                    }
                    eventBus.post(new PlacesUpdateFinished());
                },
                spiceException -> {
                    super.handleError(spiceException);
                    eventBus.post(new PlacesUpdateFinished());
                }
        );
    }

    private void updatePlacesByType(DtlPlaceType type, Collection<DtlPlace> dtlPlaces) {
        db.saveDtlPlaces(type, new ArrayList<>(dtlPlaces));
        eventBus.post(new PlacesUpdatedEvent(type));
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

        void initToolbar(DtlLocation location);
    }
}
