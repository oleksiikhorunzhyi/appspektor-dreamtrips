package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.ThinMerchantsHttpAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.mapping.ThinMerchantsTransformer;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.ThinMerchant;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class ThinMerchantsCommand extends CommandWithError<List<ThinMerchant>> implements InjectableAction {

   @Inject @Named(JanetModule.JANET_API_LIB) Janet janet;

   private final String coordinates;

   public static ThinMerchantsCommand create(String coordinates) {
      return new ThinMerchantsCommand(coordinates);
   }

   public ThinMerchantsCommand(String coordinates) {
      this.coordinates = coordinates;
   }

   @Override
   protected void run(CommandCallback<List<ThinMerchant>> callback) throws Throwable {
      callback.onProgress(0);
      janet.createPipe(ThinMerchantsHttpAction.class, Schedulers.io())
            .createObservableResult(new ThinMerchantsHttpAction(coordinates))
            .map(ThinMerchantsHttpAction::merchants)
            .compose(ThinMerchantsTransformer.INSTANCE)
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.dtl_load_merchant_error;
   }
}
