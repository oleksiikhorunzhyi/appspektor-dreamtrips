package com.worldventures.dreamtrips.modules.dtl.service;

import com.newrelic.agent.android.NewRelic;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.NewRelicTrackableAction;

import io.techery.janet.ActionPipe;
import io.techery.janet.ReadActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;
import rx.schedulers.Schedulers;

public class MerchantsInteractor {

   private final DtlLocationInteractor dtlLocationInteractor;
   private final FilterDataInteractor filterDataInteractor;
   private final ClearMemoryInteractor clearMemoryInteractor;

   private final ActionPipe<MerchantsAction> thinMerchantsPipe;

   public MerchantsInteractor(SessionActionPipeCreator sessionActionPipeCreator, DtlLocationInteractor dtlLocationInteractor,
         FilterDataInteractor filterDataInteractor, ClearMemoryInteractor clearMemoryInteractor) {

      this.dtlLocationInteractor = dtlLocationInteractor;
      this.filterDataInteractor = filterDataInteractor;
      this.clearMemoryInteractor = clearMemoryInteractor;

      thinMerchantsPipe = sessionActionPipeCreator.createPipe(MerchantsAction.class, Schedulers.io());

      connectFilterData();
      connectNewRelicTracking();
      connectForLocationUpdates();
      connectMemoryClear();
   }

   private void connectMemoryClear() {
      dtlLocationInteractor.locationSourcePipe().observe()
            .subscribe(new ActionStateSubscriber<DtlLocationCommand>()
                  .onStart(action -> clearMemoryInteractor.clearMerchantsMemoryCache()));
   }

   private void connectNewRelicTracking() {
      thinMerchantsPipe.observeSuccess()
            .cast(NewRelicTrackableAction.class)
            .map(NewRelicTrackableAction::getMetricStart)
            .subscribe(startTime ->
                  NewRelic.recordMetric("GetMerchants", "Profiler", System.currentTimeMillis() - startTime));
   }

   private void connectForLocationUpdates() {
      thinMerchantsPipe.observeSuccess()
            .map(MerchantsAction::getResult)
            .filter(thinMerchants -> !thinMerchants.isEmpty())
            .map(thinMerchants -> thinMerchants.get(0))
            .subscribe(thinMerchant -> {
               dtlLocationInteractor.locationSourcePipe().observeSuccessWithReplay()
                     .take(1)
                     .filter(DtlLocationCommand::isResultDefined)
                     .map(DtlLocationCommand::getResult)
                     .filter(dtlLocation -> dtlLocation.getLocationSourceType() == LocationSourceType.FROM_MAP ||
                           dtlLocation.getLocationSourceType() == LocationSourceType.NEAR_ME)
                     .subscribe(dtlLocation ->
                           dtlLocationInteractor.changeFacadeLocation(buildManualLocation(thinMerchant, dtlLocation))
                     );
            });
   }

   private void connectFilterData() {
      filterDataInteractor.filterDataPipe().observeSuccessWithReplay()
            .subscribe(filterDataAction -> requestMerchants());
   }

   public ReadActionPipe<MerchantsAction> thinMerchantsHttpPipe() {
      return thinMerchantsPipe;
   }

   private void requestMerchants() {
      Observable.combineLatest(
            provideFilterDataObservable(),
            provideLastFacadeLocationObservable(),
            MerchantsAction::create
      )
            .take(1)
            .subscribe(thinMerchantsPipe::send);
   }

   private Observable<FilterData> provideFilterDataObservable() {
      return filterDataInteractor.filterDataPipe().observeSuccessWithReplay()
            .take(1)
            .map(FilterDataAction::getResult);
   }

   private Observable<DtlLocation> provideLastFacadeLocationObservable() {
      return dtlLocationInteractor.locationSourcePipe().observeSuccessWithReplay()
            .take(1)
            .filter(DtlLocationCommand::isResultDefined)
            .map(DtlLocationCommand::getResult);
   }

   private static DtlLocation buildManualLocation(ThinMerchant thinMerchant, DtlLocation dtlLocation) {
      return ImmutableDtlManualLocation.copyOf((DtlManualLocation) dtlLocation)
            .withLongName(dtlLocation.getLocationSourceType() == LocationSourceType.FROM_MAP ? thinMerchant.city() : dtlLocation
                  .getLongName())
            .withAnalyticsName(thinMerchant.asMerchantAttributes().provideAnalyticsName());
   }
}
