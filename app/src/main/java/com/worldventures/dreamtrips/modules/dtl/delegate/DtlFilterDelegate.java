package com.worldventures.dreamtrips.modules.dtl.delegate;

import com.techery.spares.module.Injector;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchantAttribute;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterParameters;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.ImmutableDtlFilterData;

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

    public DtlFilterDelegate(Injector injector) {
        injector.inject(this);
        this.filterStream = BehaviorSubject.create();
    }

    public void init() {
        if (filterData == null) {
            filterData = ImmutableDtlFilterData.builder().build();
        }
        filterData = ImmutableDtlFilterData.copyOf(filterData).withDistanceType(db.getMerchantsDistanceType());
    }

    public Observable<DtlFilterData> getFilterStream(){
        return filterStream;
    }

    public void applySearch(String query) {
        filterData = ImmutableDtlFilterData.copyOf(filterData).withSearchQuery(query);
        notifySubscribers();
    }

    public void applyFilter(DtlFilterParameters filterParameters) {
        filterData = DtlFilterData.merge(filterParameters, filterData);
        notifySubscribers();
    }

    public DtlFilterData getFilterData() {
        return filterData;
    }

    public void obtainAmenities() {
        final List<DtlMerchantAttribute> amenities = db.getAmenities();
        filterData = ImmutableDtlFilterData
                .copyOf(filterData)
                .withAmenities(amenities)
                .withSelectedAmenities(amenities);
        notifySubscribers();
    }

    public void reset() {
        filterData = ImmutableDtlFilterData.copyOf(filterData).withSelectedAmenities(filterData.getAmenities());
        notifySubscribers();
    }

    public void notifySubscribers(){
        filterStream.onNext(filterData);
    }
}
