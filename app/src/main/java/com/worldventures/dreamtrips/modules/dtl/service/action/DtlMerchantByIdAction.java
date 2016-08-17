package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.DtlMerchant;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class DtlMerchantByIdAction extends Command<DtlMerchant> implements InjectableAction {

   @Inject DtlMerchantInteractor merchantInteractor;

   private final String merchantId;

   public DtlMerchantByIdAction(String merchantId) {
      this.merchantId = merchantId;
   }

   @Override
   protected void run(CommandCallback<DtlMerchant> callback) throws Throwable {
      merchantInteractor.merchantsActionPipe()
            .observeSuccessWithReplay()
            .first()
            .map(DtlMerchantsAction::getResult)
            .flatMap(Observable::from)
            .filter(merchant -> merchant.getId().equals(merchantId))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   public String getMerchantId() {
      return merchantId;
   }
}
