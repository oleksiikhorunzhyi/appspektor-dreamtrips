package com.worldventures.dreamtrips.modules.dtl.service;

import com.newrelic.agent.android.NewRelic;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;
import com.worldventures.dreamtrips.modules.dtl.service.action.LocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.NewRelicTrackableAction;

import io.techery.janet.ActionPipe;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.schedulers.Schedulers;

public class MerchantsInteractor {

   private final DtlLocationInteractor dtlLocationInteractor;
   private final ClearMemoryInteractor clearMemoryInteractor;

   private final ActionPipe<MerchantsAction> thinMerchantsPipe;

   public MerchantsInteractor(SessionActionPipeCreator sessionActionPipeCreator, DtlLocationInteractor dtlLocationInteractor,
         ClearMemoryInteractor clearMemoryInteractor) {

      this.dtlLocationInteractor = dtlLocationInteractor;
      this.clearMemoryInteractor = clearMemoryInteractor;

      this.thinMerchantsPipe = sessionActionPipeCreator.createPipe(MerchantsAction.class, Schedulers.io());

      connectNewRelicTracking();
      connectForLocationUpdates();
      connectMemoryClear();
   }

   private void connectMemoryClear() {
      dtlLocationInteractor.locationSourcePipe().observe()
            .subscribe(new ActionStateSubscriber<LocationCommand>()
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
                     .filter(LocationCommand::isResultDefined)
                     .map(LocationCommand::getResult)
                     .filter(dtlLocation -> dtlLocation.locationSourceType() == LocationSourceType.FROM_MAP ||
                           dtlLocation.locationSourceType() == LocationSourceType.NEAR_ME)
                     .subscribe(dtlLocation ->
                           dtlLocationInteractor.changeFacadeLocation(buildManualLocation(thinMerchant, dtlLocation))
                     );
            });
   }

   public ActionPipe<MerchantsAction> thinMerchantsHttpPipe() {
      return thinMerchantsPipe;
   }

   private static DtlLocation buildManualLocation(ThinMerchant thinMerchant, DtlLocation dtlLocation) {
      return ImmutableDtlLocation.copyOf(dtlLocation)
            .withLongName(dtlLocation.locationSourceType() == LocationSourceType.FROM_MAP ? thinMerchant.city() : dtlLocation
                  .longName())
            .withAnalyticsName(thinMerchant.asMerchantAttributes().provideAnalyticsName());
   }
}
