package com.worldventures.dreamtrips.modules.dtl.service;


import com.worldventures.dreamtrips.modules.dtl.model.RequestSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.service.action.LocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.RequestSourceTypeAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.ReviewMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.ImmutableMerchantsActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.MerchantsActionParams;

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
            .subscribe(this::sendMerchantsAction);
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

   private void sendMerchantsAction(MerchantsAction action) {
      merchantsInteractor.thinMerchantsHttpPipe().send(action);
      merchantsRequestSourceInteractor.requestSourceActionPipe().send(RequestSourceTypeAction.list());
   }

   private void sendReviewMerchantsAction(ReviewMerchantsAction action) {
      merchantsInteractor.reviewsMerchantsHttpPipe().send(action);
      merchantsRequestSourceInteractor.requestSourceActionPipe().send(RequestSourceTypeAction.list());
   }

   private final class MerchantsUpdateFunc implements Func2<FilterData, DtlLocation, MerchantsActionParams> {

      @Override
      public MerchantsActionParams call(FilterData filterData, DtlLocation location) {
         return ImmutableMerchantsActionParams.builder()
               .filterData(filterData)
               .location(location)
               .requestSource(requestSourceType)
               .build();
      }
   }
}
