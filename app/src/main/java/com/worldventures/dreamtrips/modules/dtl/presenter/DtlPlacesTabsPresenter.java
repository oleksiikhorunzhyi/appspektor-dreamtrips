package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.api.place.GetDtlPlacesQuery;
import com.worldventures.dreamtrips.modules.dtl.event.PlaceClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.event.PlacesUpdateFinished;
import com.worldventures.dreamtrips.modules.dtl.event.PlacesUpdatedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlace;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.DtlPlaceType;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlPlacesListFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import icepick.State;

public class DtlPlacesTabsPresenter extends Presenter<DtlPlacesTabsPresenter.View> {

    @Inject
    SnappyRepository db;
    @State
    boolean initialized;

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

        if (!initialized)
            loadPlaces();
    }

    private void loadPlaces() {
        doRequest(new GetDtlPlacesQuery(location.getLocationId()),
                this::placeLoaded,
                spiceException -> {
                    super.handleError(spiceException);
                    eventBus.post(new PlacesUpdateFinished());
                }
        );
    }

    private void placeLoaded(List<DtlPlace> dtlPlaces) {
        Map<DtlPlaceType, Collection<DtlPlace>> byTypeMap =
                Queryable.from(dtlPlaces).groupToMap(DtlPlace::getPlaceType);

        Queryable.from(byTypeMap.keySet())
                .forEachR(type -> updatePlacesByType(type, byTypeMap.get(type)));

        saveAmenities(dtlPlaces);

        eventBus.post(new PlacesUpdateFinished());
    }

    private void saveAmenities(List<DtlPlace> dtlPlaces) {
        Set<DtlPlaceAttribute> amenitiesSet = new HashSet<>();
        Queryable.from(dtlPlaces).forEachR(dtlPlace -> {
                    if (dtlPlace.getAmenities() != null)
                        amenitiesSet.addAll(dtlPlace.getAmenities());
                }
        );

        db.saveAmenities(amenitiesSet);
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

    /**
     * Analytics-related
     */
    public void trackTabChange(int newPosition) {
        String newTabName = dtlPlaceTypes.get(newPosition).equals(DtlPlaceType.OFFER) ?
                TrackingHelper.DTL_ACTION_OFFERS_TAB : TrackingHelper.DTL_ACTION_DINING_TAB;
        TrackingHelper.dtlPlacesTab(newTabName);
    }

    public void onEventMainThread(final PlaceClickedEvent event) {
        if (!view.isTabletLandscape()) {
            view.openDetails(event.getPlace());
        }
    }

    public interface View extends Presenter.View {

        void setTypes(List<DtlPlaceType> types);

        void updateSelection();

        void initToolbar(DtlLocation location);

        void openDetails(DtlPlace place);
    }
}
