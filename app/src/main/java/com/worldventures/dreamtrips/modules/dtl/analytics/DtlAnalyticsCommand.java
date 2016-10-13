package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.dtl.model.LocationSourceType;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.model.location.ImmutableDtlManualLocation;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationFacadeCommand;
import com.worldventures.dreamtrips.modules.dtl.service.action.MerchantsAction;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class DtlAnalyticsCommand extends Command<Void> implements InjectableAction {

   @Inject protected AnalyticsInteractor analyticsInteractor;
   @Inject protected DtlLocationInteractor dtlLocationInteractor;
   @Inject protected MerchantsInteractor merchantInteractor;

   protected final DtlAnalyticsAction action;

   public static DtlAnalyticsCommand create(DtlAnalyticsAction action) {
      return new DtlAnalyticsCommand(action);
   }

   public DtlAnalyticsCommand(DtlAnalyticsAction action) {
      this.action = action;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      dtlLocationInteractor.locationFacadePipe()
            .observeSuccessWithReplay()
            .map(DtlLocationFacadeCommand::getResult)
            .map(dtlLocation -> {
               if (dtlLocation.getLocationSourceType() == LocationSourceType.EXTERNAL) {
                  action.setAnalyticsLocation(dtlLocation);
               } else {
                  merchantInteractor.thinMerchantsHttpPipe()
                        .observeSuccessWithReplay()
                        .map(MerchantsAction::getResult)
                        .map(merchants -> merchants.get(0))
                        .map(dtlMerchant -> ImmutableDtlManualLocation.copyOf((DtlManualLocation) dtlLocation)
                              .withAnalyticsName(dtlMerchant.asMerchantAttributes().provideAnalyticsName()))
                        .subscribe(action::setAnalyticsLocation, throwable -> {});
               }
               return action;
            })
            .flatMap(action -> analyticsInteractor.analyticsActionPipe().createObservableResult(action))
            .map(baseAnalyticsAction -> (Void) null)
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
