package com.worldventures.dreamtrips.modules.dtl.analytics;

import com.worldventures.dreamtrips.modules.dtl.model.merchant.filter.FilterData;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.FilterDataAction;

import javax.inject.Inject;

import io.techery.janet.command.annotations.CommandAction;

@CommandAction
public class MerchantDetailsViewCommand extends DtlAnalyticsCommand {

   @Inject FilterDataInteractor filterDataInteractor;

   public MerchantDetailsViewCommand(MerchantDetailsViewEvent action) {
      super(action);
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      filterDataInteractor.filterDataPipe()
            .observeSuccessWithReplay()
            .take(1)
            .map(FilterDataAction::getResult)
            .map(FilterData::isOffersOnly)
            .flatMap(isOffersOnly -> {
               ((MerchantDetailsViewEvent) action).setOffersOnly(isOffersOnly);
               return null;
            })
            .subscribe(aVoid -> {
               try {
                  super.run(callback);
               } catch (Throwable throwable) {
                  callback.onFail(throwable);
               }
            }, callback::onFail);
   }
}
