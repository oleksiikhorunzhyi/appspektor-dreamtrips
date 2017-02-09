package com.worldventures.dreamtrips.wallet.service.nxt;

import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.model.ImmutableMultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;
import com.worldventures.dreamtrips.wallet.service.nxt.util.DetokenizedBankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCardHelper;

import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class DetokenizeBankCardCommand extends BaseNxtCommand<NxtBankCard> {

   public DetokenizeBankCardCommand(BankCard card) {
      super(card);
   }

   @Override
   protected void run(CommandCallback<NxtBankCard> callback) throws Exception {
      janet.createPipe(MultifunctionNxtHttpAction.class, Schedulers.io())
            .createObservableResult(new MultifunctionNxtHttpAction(createRequestBody()))
            .map(detokenizeBankCardHttpAction -> createResponseBody(detokenizeBankCardHttpAction.getResponse()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private MultiRequestBody createRequestBody() {
      return ImmutableMultiRequestBody.builder()
            .sessionToken(getSessionToken())
            .multiRequestElements(NxtBankCardHelper.getDataForDetokenization(card))
            .build();
   }

   private NxtBankCard createResponseBody(MultiResponseBody response) {
      return DetokenizedBankCard.from(card, response);
   }

}