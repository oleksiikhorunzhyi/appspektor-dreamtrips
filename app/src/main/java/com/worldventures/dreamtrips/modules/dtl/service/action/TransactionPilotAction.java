package com.worldventures.dreamtrips.modules.dtl.service.action;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.api.dtl.merchants.GetTransactionRequestPilotAction;
import com.worldventures.dreamtrips.core.api.action.CommandWithError;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.modules.dtl.model.merchant.thrst.GetTransactionResponse;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.TransactionThrstActionParams;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.TransactionThrstCreator;

import javax.inject.Inject;

import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.mappery.MapperyContext;

@CommandAction
public class TransactionPilotAction extends CommandWithError<GetTransactionResponse> implements InjectableAction {

   @Inject Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject TransactionThrstCreator transactionCreator;

   private final TransactionThrstActionParams reviewParams;

   public static TransactionPilotAction create(TransactionThrstActionParams reviewParams) {
      return new TransactionPilotAction(reviewParams);
   }

   public TransactionPilotAction(TransactionThrstActionParams reviewParams) {
      this.reviewParams = reviewParams;
   }

   @Override
   protected void run(CommandCallback<GetTransactionResponse> callback) throws Throwable {
      callback.onProgress(0);
      janet.createPipe(GetTransactionRequestPilotAction.class)
            .createObservableResult(transactionCreator.createAction(reviewParams))
            .map(GetTransactionRequestPilotAction::transactionDetails)
            .map(attributes -> mapperyContext.convert(attributes, GetTransactionResponse.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   @Override
   public int getFallbackErrorMessage() {
      return R.string.dtl_load_error;
   }
}
