package com.worldventures.dreamtrips.modules.dtl.delegate;

import com.innahema.collections.query.queriables.Queryable;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
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

public class DtlMerchantDelegate {

    private SnappyRepository db;

    private RequestingPresenter requestingPresenter;
    //
    private List<DtlMerchantType> dtlMerchantTypes;
    private List<DtlMerchant> merchants;
    //
    private List<MerchantUpdatedListener> listeners = new ArrayList<>();

    public DtlMerchantDelegate(SnappyRepository db) {
        this.db = db;
        dtlMerchantTypes = Arrays.asList(DtlMerchantType.OFFER, DtlMerchantType.DINING);
    }

    public List<DtlMerchantType> getDtlMerchantTypes() {
        return dtlMerchantTypes;
    }

    public void setRequestingPresenter(RequestingPresenter requestingPresenter) {
        this.requestingPresenter = requestingPresenter;
    }

    public void loadMerchants(DtlLocation location) {
        requestingPresenter.doRequest(new GetNearbyMerchantsRequest(location), this::placeLoaded);
    }

    public void onMerchantLoadingError(SpiceException spiceException) {
        Queryable.from(listeners).forEachR(listener -> listener.onMerchantsFailed(spiceException));
    }

    public List<DtlMerchant> getMerchants() {
        return merchants == null ? getCachedMerchants() : merchants;
    }

    public void attachListener(MerchantUpdatedListener merchantUpdatedListener) {
        listeners.add(merchantUpdatedListener);
    }

    public void detachListener(MerchantUpdatedListener merchantUpdatedListener) {
        listeners.remove(merchantUpdatedListener);
    }

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

    private List<DtlMerchant> getCachedMerchants() {
        merchants = db.getDtlMerchants();
        return merchants;
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

    public interface MerchantUpdatedListener {
        void onMerchantsUploaded();

        void onMerchantsFailed(SpiceException spiceException);
    }
}
