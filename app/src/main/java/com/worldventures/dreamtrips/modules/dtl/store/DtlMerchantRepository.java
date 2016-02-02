package com.worldventures.dreamtrips.modules.dtl.store;

import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.DtlApi;
import com.worldventures.dreamtrips.core.api.factory.RxApiFactory;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import techery.io.library.Job1Executor;
import timber.log.Timber;

public class DtlMerchantRepository {

    @Inject
    SnappyRepository db;
    @Inject
    DtlApi dtlApi;
    @Inject
    RxApiFactory apiFactory;

    private List<DtlMerchantType> dtlMerchantTypes;
    private List<DtlMerchant> merchants;
    //
    public final Job1Executor<String, ArrayList<DtlMerchant>> getMerchantsExecutor =
            new Job1Executor<>(this::getMerchants);

    public DtlMerchantRepository(Injector injector) {
        injector.inject(this);
        dtlMerchantTypes = Arrays.asList(DtlMerchantType.OFFER, DtlMerchantType.DINING);
    }

    public List<DtlMerchantType> getDtlMerchantTypes() {
        return dtlMerchantTypes;
    }

    /**
     * Loads merchants {@link DtlMerchant} for the specified location
     * @param location {@link DtlLocation} current location
     */
    public void loadMerchants(DtlLocation dtlLocation) {
        getMerchantsExecutor.createJobWith(dtlLocation.asStringLatLong()).subscribe();
    }

    private void onMerchantsLoaded(List<DtlMerchant> dtlMerchants) {
        this.merchants = dtlMerchants;
        db.saveDtlMerhants(merchants);
        saveAmenities(dtlMerchants);
    }

    private void saveAmenities(List<DtlMerchant> dtlMerchants) {
        Set<DtlMerchantAttribute> amenitiesSet = new HashSet<>();
        Queryable.from(dtlMerchants).forEachR(dtlMerchant -> {
                    if (dtlMerchant.getAmenities() != null)
                        amenitiesSet.addAll(dtlMerchant.getAmenities());
                }
        );
        //
        db.saveAmenities(amenitiesSet);
    }

    private Observable<ArrayList<DtlMerchant>> getMerchants(String ll) {
        return apiFactory.composeApiCall(() -> dtlApi.getNearbyDtlMerchants(ll))
                .doOnNext(this::onMerchantsLoaded);
    }

    /**
     * Clean all the merchant related data
     */
    public void clean() {
        if (merchants != null) merchants.clear();
        db.clearMerchantData();
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
     * @param id merchant Id
     * @return merchant with provided id
     */
    public DtlMerchant getMerchantById(String id) {
        return Queryable.from(getMerchants()).firstOrDefault(merchant -> merchant.getId().equals(id));
    }

    private void getCachedMerchants() {
        merchants = db.getDtlMerchants();
    }

    /**
     * Logs a warning if no listener of desired type attached.<br />
     * Helps to figure out possible fucked-up setup faster.
     *
     * @param listenersLists all lists of listeners that are to be triggered
     */
    protected void checkListeners(List... listenersLists) {
        for(List list : listenersLists) {
            if (list.isEmpty())
                Timber.w("Checking store listeners: no registered listener of desired type! Check your setup.");
        }
    }
}
