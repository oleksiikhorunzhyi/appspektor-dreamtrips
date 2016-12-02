package com.worldventures.dreamtrips.modules.dtl.service;


import com.worldventures.dreamtrips.modules.dtl.model.RequestSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.service.action.LocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.RequestSourceTypeAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.MerchantsParamsBundle;

import rx.Observable;
import rx.functions.Func2;

public class MerchantsFacadeInteractor {

   private final FilterDataInteractor filterDataInteractor;
   private final DtlLocationInteractor locationInteractor;
   private final MerchantsInteractor merchantsInteractor;
   private final MerchantsRequestSourceInteractor merchantsRequestSourceInteractor;

   private RequestSourceType requestSourceType;

   public MerchantsFacadeInteractor(MerchantsRequestSourceInteractor merchantsRequestSourceInteractor,
         FilterDataInteractor filterDataInteractor, MerchantsInteractor merchantsInteractor, DtlLocationInteractor locationInteractor) {

      this.filterDataInteractor = filterDataInteractor;
      this.merchantsRequestSourceInteractor = merchantsRequestSourceInteractor;
      this.locationInteractor = locationInteractor;
      this.merchantsInteractor = merchantsInteractor;

      this.connectFilterData();
      this.connectRequestSource();
   }

   private void connectFilterData() {
      filterDataInteractor.filterDataPipe().observeSuccessWithReplay()
            .subscribe(filterDataAction -> requestMerchants());
   }

   private void connectRequestSource() {
      merchantsRequestSourceInteractor.requestSourceActionPipe().observeSuccessWithReplay()
            .map(RequestSourceTypeAction::getResult)
            .subscribe(type -> this.requestSourceType = type);
   }

   public void requestMerchants() {
      Observable.combineLatest(
            provideFilterDataObservable(),
            provideLastSourceLocationObservable(),
            new MerchantsUpdateFunc()
      )
            .take(1)
            .map(MerchantsAction::create)
            .subscribe(action -> {
               merchantsInteractor.thinMerchantsHttpPipe().send(action);
               merchantsRequestSourceInteractor.requestSourceActionPipe().send(RequestSourceTypeAction.list());
            });
   }

   private Observable<FilterData> provideFilterDataObservable() {
      return filterDataInteractor.filterDataPipe().observeSuccessWithReplay()
            .take(1)
            .map(FilterDataAction::getResult);
   }

   private Observable<DtlLocation> provideLastSourceLocationObservable() {
      return locationInteractor.locationSourcePipe().observeSuccessWithReplay()
            .take(1)
            .filter(LocationCommand::isResultDefined)
            .map(LocationCommand::getResult);
   }

   private final class MerchantsUpdateFunc implements Func2<FilterData, DtlLocation, MerchantsParamsBundle> {

      @Override
      public MerchantsParamsBundle call(FilterData filterData, DtlLocation location) {
         return MerchantsParamsBundle.create(filterData, location, requestSourceType);
      }
   }
}
