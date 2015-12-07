package com.worldventures.dreamtrips.modules.dtl.store;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.modules.common.presenter.RequestingPresenter;
import com.worldventures.dreamtrips.modules.dtl.api.place.GetNearbyMerchantsRequest;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DtlMerchantStore extends BaseStore {

    private SnappyRepository db;
    private RequestingPresenter requestingPresenter;
    //
    private List<DtlMerchantType> dtlMerchantTypes;
    private List<DtlMerchant> merchants;
    //
    private List<MerchantUpdatedListener> listeners = new ArrayList<>();

    public DtlMerchantStore(SnappyRepository db) {
        this.db = db;
        dtlMerchantTypes = Arrays.asList(DtlMerchantType.OFFER, DtlMerchantType.DINING);
    }

    public List<DtlMerchantType> getDtlMerchantTypes() {
        return dtlMerchantTypes;
    }

    /**
     * set RequestingPresenter in {@link Presenter#onInjected()}
     *
     * @param requestingPresenter {@link RequestingPresenter} presenter to execute Requests
     */
    public void setRequestingPresenter(RequestingPresenter requestingPresenter) {
        this.requestingPresenter = requestingPresenter;
    }

    /**
     * loads merchants {@link DtlMerchant} for the specified location
     * @param location {@link DtlLocation} current location
     */
    public void loadMerchants(DtlLocation location) {
        checkFields();
        //
        requestingPresenter.doRequest(new GetNearbyMerchantsRequest(location), this::placeLoaded);
    }

    /**
     * should be called in {@link Presenter#handleError(SpiceException)} to notify listeners about
     * failed merchant loading
     * @param spiceException actual exception
     */
    public void onMerchantLoadingError(SpiceException spiceException) {
        Queryable.from(listeners).forEachR(listener -> listener.onMerchantsFailed(spiceException));
    }

    /**
     * Return loaded merchants or empty arrayList {@link ArrayList}
     * @return list of current merchants
     */
    public List<DtlMerchant> getMerchants() {
        if (merchants == null) getCachedMerchants();
        return merchants;
    }

    /**
     * Attach listener to {@link DtlMerchantStore} to notify about changes
     * should be called in {@link Presenter#onInjected()}
     * @param merchantUpdatedListener listener
     */
    public void attachListener(MerchantUpdatedListener merchantUpdatedListener) {
        listeners.add(merchantUpdatedListener);
    }

    /**
     * Detach listener from {@link DtlMerchantStore}
     * should be called in {@link Presenter#dropView()}
     * @param merchantUpdatedListener listener
     */
    public void detachListener(MerchantUpdatedListener merchantUpdatedListener) {
        listeners.remove(merchantUpdatedListener);
    }

    /**
     * @param id merchant Id
     * @return merchant with provided id
     */
    public DtlMerchant getMerchantById(String id) {
        return Queryable.from(getMerchants()).firstOrDefault(merchant -> merchant.getId().equals(id));
    }

    private void placeLoaded(List<DtlMerchant> dtlMerchants) {
        this.merchants = dtlMerchants;
        db.saveDtlMerhants(merchants);
        saveAmenities(dtlMerchants);
        //
        Queryable.from(listeners).forEachR(MerchantUpdatedListener::onMerchantsUploaded);
    }

    private void getCachedMerchants() {
        merchants = db.getDtlMerchants();
    }

    private void saveAmenities(List<DtlMerchant> DtlMerchants) {
        Set<DtlMerchantAttribute> amenitiesSet = new HashSet<>();
        Queryable.from(DtlMerchants).forEachR(dtlPlace -> {
                    if (dtlPlace.getAmenities() != null)
                        amenitiesSet.addAll(dtlPlace.getAmenities());
                }
        );

        db.saveAmenities(amenitiesSet);
    }

    private void checkFields() {
        if (requestingPresenter == null)
            throw new IllegalStateException("You should set RequestingPresenter before loading anything");
    }

    public interface MerchantUpdatedListener {
        void onMerchantsUploaded();

        void onMerchantsFailed(SpiceException spiceException);
    }
}
