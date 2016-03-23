package com.worldventures.dreamtrips.modules.dtl.store;

import android.annotation.SuppressLint;
import android.location.Location;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.innahema.collections.query.queriables.Queryable;
import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.api.DtlApi;
import com.worldventures.dreamtrips.core.api.factory.RxApiFactory;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.DistanceType;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantType;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterParameters;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlMerchantsPredicate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableDtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableDtlFilterParameters;
import com.worldventures.dreamtrips.modules.settings.model.Setting;
import com.worldventures.dreamtrips.modules.settings.util.SettingsFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;
import techery.io.library.Job0Executor;
import techery.io.library.Job1Executor;
import techery.io.library.JobToValue;

public class DtlMerchantManager {

    @Inject
    SnappyRepository db;
    @Inject
    DtlApi dtlApi;
    @Inject
    RxApiFactory apiFactory;
    @Inject
    LocationDelegate locationDelegate;
    @Inject
    DtlLocationManager dtlLocationManager;
    //
    private final PublishSubject<DtlFilterData> filterStream = PublishSubject.create();
    //
    public static final List<DtlMerchantType> MERCHANT_TYPES = Arrays.asList(DtlMerchantType.values());
    //
    private List<DtlMerchant> merchants;
    private DtlFilterData filterData;
    //
    public final Job1Executor<String, List<DtlMerchant>> getMerchantsExecutor =
            new Job1Executor<>(this::loadAndProcessMerchants);

    public final Job0Executor<List<DtlMerchant>> merchantsResultPipe =
            new Job0Executor<>(() -> getMerchantsExecutor.connect().compose(new JobToValue<>()));

    public DtlMerchantManager(Injector injector) {
        injector.inject(this);
        initFilterData();
        merchantsResultPipe.executeJob();
    }

    private Observable<List<DtlMerchant>> loadAndProcessMerchants(String ll) {
        return Observable.combineLatest(
                combineLocationObservable(),
                apiFactory.composeApiCall(() -> dtlApi.getNearbyDtlMerchants(ll))
                        .doOnNext(this::processAmenities),
                filterStream.asObservable().take(1),
                this::filterMerchants);
    }

    private Observable<LatLng> combineLocationObservable() {
        return locationDelegate.getLastKnownLocation()
                .onErrorResumeNext(Observable.just(dtlLocationManager.getCachedSelectedLocation()
                        .getCoordinates().asAndroidLocation()))
                .flatMap(location -> Observable.just(DtlLocationHelper.selectAcceptableLocation(location,
                        dtlLocationManager.getCachedSelectedLocation())));
    }

    private List<DtlMerchant> filterMerchants(LatLng latLng, List<DtlMerchant> dtlMerchants, DtlFilterData filterData) {
        DtlMerchantsPredicate predicate = DtlMerchantsPredicate.fromFilterData(filterData);
        //
        return Observable.from(dtlMerchants)
                .doOnNext(merchant ->
                        patchMerchantDistance(merchant, latLng, filterData.getDistanceType()))
                .toSortedList(DtlMerchant.DISTANCE_COMPARATOR::compare)
                .doOnNext(this::cacheAndPersistMerchants)
                .doOnNext(this::tryUpdateLocation)
                .flatMap(Observable::from)
                .filter(predicate::apply)
                .toList()
                .doOnNext(dtlMerchants2 ->
                        trackMerchantSearch(dtlMerchants2, filterData.getSearchQuery()))
                .toBlocking().firstOrDefault(Collections.emptyList());
    }

    private void patchMerchantDistance(DtlMerchant merchant, LatLng currentLatLng,
                                       DistanceType distanceType) {
        merchant.setDistanceType(distanceType);
        merchant.setDistance(DtlLocationHelper.calculateDistance(
                currentLatLng, merchant.getCoordinates().asLatLng()));
    }

    private void trackMerchantSearch(List<DtlMerchant> merchants, String query) {
        if (!TextUtils.isEmpty(query)) TrackingHelper.dtlMerchantSearch(query, merchants.size());
    }

    public void initFilterData() {
        if (filterData == null) {
            filterData = ImmutableDtlFilterData.builder().build();
        }
        Setting distanceSetting = Queryable.from(db.getSettings()).filter(setting ->
                setting.getName().equals(SettingsFactory.DISTANCE_UNITS)).firstOrDefault();
        filterData = ImmutableDtlFilterData.copyOf(filterData)
                .withDistanceType(DistanceType.provideFromSetting(distanceSetting));
        filterStream.onNext(filterData);
    }

    private void processAmenities(List<DtlMerchant> dtlMerchants) {
        saveAmenities(dtlMerchants);
        final List<DtlMerchantAttribute> amenities = db.getAmenities();
        filterData = ImmutableDtlFilterData
                .copyOf(filterData)
                .withAmenities(amenities)
                .withSelectedAmenities(amenities);
        filterStream.onNext(filterData);
    }

    public void applySearch(String query) {
        filterData = ImmutableDtlFilterData.copyOf(filterData).withSearchQuery(query);
        filterStream.onNext(filterData);
    }

    public void applyFilter(DtlFilterParameters filterParameters) {
        filterData = DtlFilterData.merge(filterParameters, filterData);
        filterStream.onNext(filterData);
        TrackingHelper.dtlMerchantFilter(filterData);
    }

    public void reset() {
        final DtlFilterParameters defaultParameters = ImmutableDtlFilterParameters.builder()
                .selectedAmenities(db.getAmenities())
                .build();
        filterData = DtlFilterData.merge(defaultParameters, filterData);
        filterStream.onNext(filterData);
    }

    public String getCurrentQuery() {
        return filterData.getSearchQuery();
    }

    public DtlFilterData getFilterData() {
        return filterData;
    }

    /**
     * Loads merchants {@link DtlMerchant} for the specified latitude&longitude
     */
    @SuppressLint("DefaultLocale")
    public void loadMerchants(Location location) {
        getMerchantsExecutor.createJobWith(String.format("%1$f,%2$f",
                    location.getLatitude(), location.getLongitude()))
                .subscribe();
    }

    private void cacheAndPersistMerchants(List<DtlMerchant> dtlMerchants) {
        this.merchants = dtlMerchants;
        db.saveDtlMerhants(merchants);
    }

    private void tryUpdateLocation(List<DtlMerchant> dtlMerchants) {
        if (dtlLocationManager.getSelectedLocation().getLocationSourceType() == LocationSourceType.FROM_MAP &&
                !dtlMerchants.isEmpty()) {
            DtlLocation updatedLocation = ImmutableDtlManualLocation
                    .copyOf((DtlManualLocation) dtlLocationManager.getSelectedLocation())
                    .withLongName(dtlMerchants.get(0).getCity());
            dtlLocationManager.persistLocation(updatedLocation);
        }
    }

    /**
     * Creates a distinctive set of merchant attributes - from every attribute in all merchants
     */
    private void saveAmenities(List<DtlMerchant> dtlMerchants) {
        Set<DtlMerchantAttribute> amenitiesSet = new HashSet<>();
        Queryable.from(dtlMerchants).forEachR(dtlMerchant -> {
                    if (dtlMerchant.getAmenities() != null)
                        amenitiesSet.addAll(dtlMerchant.getAmenities());
                }
        );
        //
        db.saveAmenities(Queryable.from(amenitiesSet)
                .sort(DtlMerchantAttribute.NAME_ALPHABETIC_COMPARATOR)
                .toList());
    }

    /**
     * Clean all merchants from in-memory cache and from persistent storage
     */
    public void clean() {
        if (merchants != null) merchants.clear();
        db.clearMerchantData();
    }

    /**
     * Return loaded merchants or empty arrayList {@link ArrayList}
     *
     * @return list of current merchants
     */
    public List<DtlMerchant> getMerchants() {
        return merchants == null ? Collections.emptyList() : merchants;
    }

    /**
     * Return merchant with given id or null
     *
     * @param id merchant Id
     * @return merchant with provided id
     */
    public DtlMerchant getMerchantById(String id) {
        return Queryable.from(getMerchants()).firstOrDefault(merchant -> merchant.getId().equals(id));
    }
}
