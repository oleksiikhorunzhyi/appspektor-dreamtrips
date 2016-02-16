package com.worldventures.dreamtrips.modules.dtl.delegate;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterParameters;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableDtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableDtlFilterParameters;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class DtlFilterDelegate {

    @Inject
    SnappyRepository db;
    //
    private DtlFilterData filterData;
    //
    private final BehaviorSubject<DtlFilterData> filterStream;

    /**
     * Instantiates a new Dtl filter delegate.
     * @param injector the injector
     */
    public DtlFilterDelegate(Injector injector) {
        injector.inject(this);
        this.filterStream = BehaviorSubject.create();
    }

    /**
     * Create filter data with default parameters
     */
    public void init() {
        if (filterData == null) {
            filterData = ImmutableDtlFilterData.builder().build();
        }
        filterData = ImmutableDtlFilterData.copyOf(filterData).withDistanceType(db.getMerchantsDistanceType());
    }

    /**
     * Gets filter stream.
     * @return the filter stream
     */
    public Observable<DtlFilterData> getFilterStream() {
        return filterStream;
    }

    /**
     * Apply search with selected query
     * @param query the search query
     */
    public void applySearch(String query) {
        filterData = ImmutableDtlFilterData.copyOf(filterData).withSearchQuery(query);
        notifySubscribers();
    }

    /**
     * Apply filter.
     * @param filterParameters the filter parameters
     */
    public void applyFilter(DtlFilterParameters filterParameters) {
        filterData = DtlFilterData.merge(filterParameters, filterData);
        notifySubscribers();
    }

    /**
     * Gets filer data
     * @return the
     */
    public DtlFilterData getFilterData() {
        return filterData;
    }

    /**
     * Attach amenities to filter
     */
    public void obtainAmenities() {
        final List<DtlMerchantAttribute> amenities = db.getAmenities();
        filterData = ImmutableDtlFilterData
                .copyOf(filterData)
                .withAmenities(amenities)
                .withSelectedAmenities(amenities);
        notifySubscribers();
    }

    /**
     * Reset filter
     */
    public void reset() {
        final DtlFilterParameters defaultParameters = ImmutableDtlFilterParameters.builder().selectedAmenities(db.getAmenities()).build();
        filterData = DtlFilterData.merge(defaultParameters, filterData);
        notifySubscribers();
    }

    /**
     * Notify subscribers when filter is changed
     */
    public void notifySubscribers() {
        filterStream.onNext(filterData);
    }
}
