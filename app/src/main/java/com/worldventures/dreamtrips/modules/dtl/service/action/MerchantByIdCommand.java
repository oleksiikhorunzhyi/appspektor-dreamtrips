package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.MerchantByIdHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.mapping.MerchantMapper;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.Merchant;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class MerchantByIdCommand extends CommandWithError<Merchant> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;

   private final String offerId;
   private final String merchantId;

   public static MerchantByIdCommand create(String merchantId) {
      return create(merchantId, null);
   }

   public static MerchantByIdCommand create(String merchantId, String offerId) {
      return new MerchantByIdCommand(merchantId, offerId);
   }

   public MerchantByIdCommand(String merchantId, String offerId) {
      this.merchantId = merchantId;
      this.offerId = offerId;
   }

   @Override
   protected void run(CommandCallback<Merchant> callback) throws Throwable {
      callback.onProgress(0);
      janet.createPipe(MerchantByIdHttpAction.class, Schedulers.io())
            .createObservableResult(new MerchantByIdHttpAction(merchantId))
            .map(MerchantByIdHttpAction::merchant)
            .map(MerchantMapper.INSTANCE::convert)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   public String getOfferId() {
      return offerId;
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.dtl_load_merchant_error;
   }
}
