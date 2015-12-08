package com.worldventures.dreamtrips.modules.dtl.presenter;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.dtl.api.merchant.GetDtlMerchantsQuery;
import com.worldventures.dreamtrips.modules.dtl.event.MerchantClickedEvent;
import com.worldventures.dreamtrips.modules.dtl.event.MerchantsUpdateFinished;
import com.worldventures.dreamtrips.modules.dtl.event.MerchantUpdatedEvent;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.view.fragment.DtlMerchantsListFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import icepick.State;

public class DtlMerchantsTabsPresenter extends Presenter<DtlMerchantsTabsPresenter.View> {

    @Inject
    SnappyRepository db;
    @State
    boolean initialized;

    private DtlLocation location;
    private List<DtlMerchantType> dtlMerchantTypes;

    public DtlMerchantsTabsPresenter(@Nullable DtlLocation location) {
        if (location == null) {
            location = db.getSelectedDtlLocation();
        }
        this.location = location;
        dtlMerchantTypes = Arrays.asList(DtlMerchantType.OFFER, DtlMerchantType.DINING);
    }

    @Override
    public void takeView(View view) {
        super.takeView(view);
        view.initToolbar(location);
        setTabs();

        if (!initialized)
            loadMerchants();

        initialized = true;
    }

    private void loadMerchants() {
        doRequest(new GetDtlMerchantsQuery(location.getId()), this::merchantsLoaded);
    }

    @Override
    public void handleError(SpiceException error) {
        super.handleError(error);
        eventBus.post(new MerchantsUpdateFinished());
    }

    private void merchantsLoaded(List<DtlMerchant> DtlMerchants) {
        Map<DtlMerchantType, Collection<DtlMerchant>> byTypeMap =
                Queryable.from(DtlMerchants).groupToMap(DtlMerchant::getMerchantType);

        Queryable.from(byTypeMap.keySet())
                .forEachR(type -> updateMerchantsByType(type, byTypeMap.get(type)));

        saveAmenities(DtlMerchants);

        eventBus.post(new MerchantsUpdateFinished());
    }

    private void saveAmenities(List<DtlMerchant> DtlMerchants) {
        Set<DtlMerchantAttribute> amenitiesSet = new HashSet<>();
        Queryable.from(DtlMerchants).forEachR(dtlMerchant -> {
                    if (dtlMerchant.getAmenities() != null)
                        amenitiesSet.addAll(dtlMerchant.getAmenities());
                }
        );

        db.saveAmenities(amenitiesSet);
    }

    private void updateMerchantsByType(DtlMerchantType type, Collection<DtlMerchant> DtlMerchants) {
        db.saveDtlMerchants(type, new ArrayList<>(DtlMerchants));
        eventBus.post(new MerchantUpdatedEvent(type));
    }

    public void setTabs() {
        view.setTypes(dtlMerchantTypes);
        view.updateSelection();
    }

    public Bundle prepareArgsForTab(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DtlMerchantsListFragment.EXTRA_TYPE, dtlMerchantTypes.get(position));
        return bundle;
    }

    /**
     * Analytics-related
     */
    public void trackTabChange(int newPosition) {
        String newTabName = dtlMerchantTypes.get(newPosition).equals(DtlMerchantType.OFFER) ?
                TrackingHelper.DTL_ACTION_OFFERS_TAB : TrackingHelper.DTL_ACTION_DINING_TAB;
        TrackingHelper.dtlMerchantsTab(newTabName);
    }

    public void onEventMainThread(final MerchantClickedEvent event) {
        if (!view.isTabletLandscape()) {
            view.openDetails(event.getDtlMerchant());
        }
    }

    public interface View extends Presenter.View {

        void setTypes(List<DtlMerchantType> types);

        void updateSelection();

        void initToolbar(DtlLocation location);

        void openDetails(DtlMerchant merchant);
    }
}
