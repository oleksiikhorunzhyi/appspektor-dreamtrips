package com.worldventures.dreamtrips.modules.dtl.service;

import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.DtlFilterData;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.ReadActionPipe;
import rx.Observable;
import rx.schedulers.Schedulers;

public class DtlFilterMerchantService {

    private final ActionPipe<DtlFilterDataAction> filterDataPipe;

    private final ActionPipe<DtlFilterMerchantsAction> filterMerchantsPipe;


    public DtlFilterMerchantService(DtlMerchantService dtlMerchantService,
                                    DtlLocationService locationService,
                                    LocationDelegate locationDelegate,
                                    Janet janet) {
        filterMerchantsPipe = janet.createPipe(DtlFilterMerchantsAction.class, Schedulers.io());
        filterDataPipe = janet.createPipe(DtlFilterDataAction.class, Schedulers.io());

        filterDataPipe.send(DtlFilterDataAction.init());
        filterDataPipe.observeSuccess()
                .subscribe(action -> filterMerchantsPipe.send(new DtlFilterMerchantsAction(action.getResult(), dtlMerchantService.merchantsActionPipe(), locationService.locationPipe(), locationDelegate)));
    }

    public ActionPipe<DtlFilterDataAction> filterDataPipe() {
        return filterDataPipe;
    }

    public ReadActionPipe<DtlFilterMerchantsAction> filterMerchantsActionPipe() {
        return filterMerchantsPipe;
    }

    public Observable<DtlFilterData> getFilterData() {
        return filterDataPipe.observeSuccessWithReplay()
                .first()
                .map(DtlFilterDataAction::getResult);
    }

}
