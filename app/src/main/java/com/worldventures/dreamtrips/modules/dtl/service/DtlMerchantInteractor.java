package com.worldventures.dreamtrips.modules.dtl.service;

import com.innahema.collections.query.queriables.Queryable;
import com.newrelic.agent.android.NewRelic;
import com.worldventures.dreamtrips.api.dtl.merchants.MerchantByIdHttpAction;
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

import java.util.List;

import io.techery.janet.ActionPipe;
import io.techery.janet.Janet;
import io.techery.janet.WriteActionPipe;
import rx.schedulers.Schedulers;

public class DtlMerchantInteractor {

   private final DtlLocationInteractor locationInteractor;

   private final ActionPipe<DtlUpdateAmenitiesAction> updateAmenitiesPipe;
   private final ActionPipe<DtlMerchantsAction> merchantsPipe;
   private final ActionPipe<DtlMerchantByIdAction> merchantByIdPipe;
   private final ActionPipe<MerchantByIdHttpAction> merchantByIdHttpPipe;

   private final WriteActionPipe<DtlFilterDataAction> filterDataActionPipe;

   public DtlMerchantInteractor(Janet janet, Janet apiLibJanet, DtlLocationInteractor locationInteractor) {
      this.locationInteractor = locationInteractor;

      updateAmenitiesPipe = janet.createPipe(DtlUpdateAmenitiesAction.class, Schedulers.io());
      merchantsPipe = janet.createPipe(DtlMerchantsAction.class, Schedulers.io());
      merchantByIdPipe = janet.createPipe(DtlMerchantByIdAction.class);
      filterDataActionPipe = janet.createPipe(DtlFilterDataAction.class, Schedulers.io());
      merchantByIdHttpPipe = apiLibJanet.createPipe(MerchantByIdHttpAction.class, Schedulers.io());

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

   public ActionPipe<MerchantByIdHttpAction> merchantByIdHttpPipe() {
      return merchantByIdHttpPipe;
   }

   //TODO: move to action
   private void tryUpdateLocation(List<DtlMerchant> dtlMerchants) {
      locationInteractor.locationPipe().createObservableResult(DtlLocationCommand.last()).filter(command -> {
         LocationSourceType sourceType = command.getResult().getLocationSourceType();
         return (sourceType == LocationSourceType.FROM_MAP || sourceType == LocationSourceType.NEAR_ME) && !dtlMerchants
               .isEmpty();
      }).map(DtlLocationCommand::getResult).subscribe(location -> {
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
