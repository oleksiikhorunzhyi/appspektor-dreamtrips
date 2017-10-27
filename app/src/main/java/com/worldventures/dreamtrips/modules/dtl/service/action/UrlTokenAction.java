package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.core.janet.CommandWithError;
import com.worldventures.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.GetUrlTokenPilotAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.GetUrlTokenResponse;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.UrlTokenActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.UrlTokenCreator;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class UrlTokenAction extends CommandWithError<GetUrlTokenResponse> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject UrlTokenCreator reviewsActionCreator;

   private String merchantId;
   private final UrlTokenActionParams reviewParams;

   public static UrlTokenAction create(String merchantId, UrlTokenActionParams reviewParams) {
      return new UrlTokenAction(merchantId, reviewParams);
   }

   public UrlTokenAction(String merchantId, UrlTokenActionParams reviewParams) {
      this.merchantId = merchantId;
      this.reviewParams = reviewParams;
   }

   @Override
   protected void run(CommandCallback<GetUrlTokenResponse> callback) throws Throwable {
      callback.onProgress(0);
      janet.createPipe(GetUrlTokenPilotAction.class)
            .createObservableResult(reviewsActionCreator.createAction(reviewParams))
            .map(GetUrlTokenPilotAction::transactionDetails)
            .map(attributes -> mapperyContext.convert(attributes, GetUrlTokenResponse.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.dtl_load_error;
   }
}
