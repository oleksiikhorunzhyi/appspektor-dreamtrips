package com.worldventures.dreamtrips.modules.dtl.service;

import com.innahema.collections.query.queriables.Queryable;
import com.newrelic.agent.android.NewRelic;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.modules.dtl.helper.DtlLocationHelper;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlFilterDataAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantByIdAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlMerchantsAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlUpdateAmenitiesAction;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantByIdCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.ThinMerchantsCommand;

import java.util.List;

import io.techery.janet.ActionPipe;
import io.techery.janet.WriteActionPipe;
import rx.schedulers.Schedulers;

public class DtlMerchantInteractor {

   private final DtlLocationInteractor locationInteractor;

   private final ActionPipe<DtlUpdateAmenitiesAction> updateAmenitiesPipe;
   private final ActionPipe<DtlMerchantsAction> merchantsPipe;
   private final ActionPipe<DtlMerchantByIdAction> merchantByIdPipe;
   private final ActionPipe<MerchantByIdCommand> merchantByIdHttpPipe;
   private final ActionPipe<ThinMerchantsCommand> thinMerchantsPipe;

   private final WriteActionPipe<DtlFilterDataAction> filterDataActionPipe;

   public DtlMerchantInteractor(SessionActionPipeCreator sessionActionPipeCreator, DtlLocationInteractor locationInteractor) {
      this.locationInteractor = locationInteractor;
      
      updateAmenitiesPipe = sessionActionPipeCreator.createPipe(DtlUpdateAmenitiesAction.class, Schedulers.io());
      merchantsPipe = sessionActionPipeCreator.createPipe(DtlMerchantsAction.class, Schedulers.io());
      merchantByIdPipe = sessionActionPipeCreator.createPipe(DtlMerchantByIdAction.class);
      filterDataActionPipe = sessionActionPipeCreator.createPipe(DtlFilterDataAction.class, Schedulers.io());
      merchantByIdHttpPipe = sessionActionPipeCreator.createPipe(MerchantByIdCommand.class, Schedulers.io());
      thinMerchantsPipe = sessionActionPipeCreator.createPipe(ThinMerchantsCommand.class, Schedulers.io());

      connectMerchantsPipe();
      connectUpdateAmenitiesPipe();
   }

   private void connectMerchantsPipe() {
      merchantsPipe.observeSuccess().filter(DtlMerchantsAction::isFromApi).subscribe(action -> {
         NewRelic.recordMetric("GetMerchants", "Profiler", (double) System.currentTimeMillis() - action.getStartTime());
         tryUpdateLocation(action.getResult());
         updateAmenitiesPipe.send(new DtlUpdateAmenitiesAction(action.getResult()));
      }, Throwable::printStackTrace);
      merchantsPipe.send(DtlMerchantsAction.restore());
   }

   private void connectUpdateAmenitiesPipe() {
      updateAmenitiesPipe.observeSuccess()
            .subscribe(action -> filterDataActionPipe.send(DtlFilterDataAction.amenitiesUpdate(action.getResult())));
   }

   public ActionPipe<DtlMerchantsAction> merchantsActionPipe() {
      return merchantsPipe;
   }

   public ActionPipe<DtlMerchantByIdAction> merchantByIdPipe() {
      return merchantByIdPipe;
   }

   public ActionPipe<MerchantByIdCommand> merchantByIdHttpPipe() {
      return merchantByIdHttpPipe;
   }

   public ActionPipe<ThinMerchantsCommand> thinMerchantsHttpPipe() {
      return thinMerchantsPipe;
   }

   //TODO: move to action
   private void tryUpdateLocation(List<DtlMerchant> dtlMerchants) {
      locationInteractor.locationPipe().observeSuccessWithReplay()
            .filter(command -> {
               LocationSourceType sourceType = command.getResult().getLocationSourceType();
               return (sourceType == LocationSourceType.FROM_MAP || sourceType == LocationSourceType.NEAR_ME) && !dtlMerchants
                     .isEmpty();
            })
            .map(DtlLocationCommand::getResult).subscribe(location -> {
         DtlMerchant nearestMerchant = Queryable.from(dtlMerchants).map(merchant -> {
            merchant.setDistance(DtlLocationHelper.calculateDistance(location.getCoordinates()
                  .asLatLng(), merchant.getCoordinates().asLatLng()));
            return merchant;
         }).sort(DtlMerchant.DISTANCE_COMPARATOR::compare).first();
         DtlLocation updatedLocation = ImmutableDtlManualLocation.copyOf((DtlManualLocation) location)
               .withLongName(location.getLocationSourceType() == LocationSourceType.FROM_MAP ? nearestMerchant.getCity() : location
                     .getLongName())
               .withAnalyticsName(nearestMerchant.getAnalyticsName());
         locationInteractor.locationPipe().send(DtlLocationCommand.change(updatedLocation));
      }, Throwable::printStackTrace);
   }
}
