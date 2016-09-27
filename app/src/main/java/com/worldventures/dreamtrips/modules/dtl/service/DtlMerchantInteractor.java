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
import com.worldventures.dreamtrips.modules.dtl.service.action.NewRelicTrackableAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.ThinMerchantsCommand;

import io.techery.janet.ActionPipe;
import io.techery.janet.ReadActionPipe;
import rx.Observable;
import rx.schedulers.Schedulers;

public class DtlMerchantInteractor {

   private final DtlLocationInteractor dtlLocationInteractor;
   private final FilterDataInteractor filterDataInteractor;

   private final ActionPipe<ThinMerchantsCommand> thinMerchantsPipe;

   public DtlMerchantInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         DtlLocationInteractor dtlLocationInteractor, FilterDataInteractor filterDataInteractor) {

      this.dtlLocationInteractor = dtlLocationInteractor;
      this.filterDataInteractor = filterDataInteractor;

      thinMerchantsPipe = sessionActionPipeCreator.createPipe(ThinMerchantsCommand.class, Schedulers.io());

      connectFilterData();
      connectNewRelicTracking();
      connectForLocationUpdates();
   }

   private void connectNewRelicTracking() {
      thinMerchantsPipe.observeSuccessWithReplay()
            .cast(NewRelicTrackableAction.class)
            .map(NewRelicTrackableAction::getMetricStart)
            .subscribe(startTime ->
                  NewRelic.recordMetric("GetMerchants", "Profiler", System.currentTimeMillis() - startTime));
   }

   private void connectForLocationUpdates() {
      thinMerchantsPipe.observeSuccessWithReplay()
            .map(ThinMerchantsCommand::getResult)
            .filter(thinMerchants -> !thinMerchants.isEmpty())
            .map(thinMerchants -> thinMerchants.get(0))
            .subscribe(thinMerchant -> {
               provideLastLocationObservable()
                     .filter(dtlLocation -> dtlLocation.getLocationSourceType() == LocationSourceType.FROM_MAP ||
                           dtlLocation.getLocationSourceType() == LocationSourceType.NEAR_ME)
                     .subscribe(dtlLocation ->
                           dtlLocationInteractor.change(buildManualLocation(thinMerchant, dtlLocation)));
            });
   }

   private void connectFilterData() {
      filterDataInteractor.filterDataPipe().observeSuccessWithReplay()
            .subscribe(filterDataAction -> requestMerchants());
   }

   public ReadActionPipe<ThinMerchantsCommand> thinMerchantsHttpPipe() {
      return thinMerchantsPipe;
   }

   private void requestMerchants() {
      Observable.combineLatest(
            provideFilterDataObservable(),
            provideLastLocationObservable(),
            ThinMerchantsCommand::create
      )
            .take(1)
            .subscribe(thinMerchantsPipe::send);
   }

   private Observable<FilterData> provideFilterDataObservable() {
      return filterDataInteractor.filterDataPipe().observeSuccessWithReplay()
            .take(1)
            .map(FilterDataAction::getResult);
   }

   private Observable<DtlLocation> provideLastLocationObservable() {
      return dtlLocationInteractor.locationPipe().observeSuccessWithReplay()
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
