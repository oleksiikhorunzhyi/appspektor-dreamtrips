package com.worldventures.dreamtrips.wallet.service.nxt;

import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.model.ImmutableMultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCardHelper;
import com.worldventures.dreamtrips.wallet.service.nxt.util.TokenizedBankCard;

import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

@CommandAction
public class TokenizeBankCardCommand extends BaseNxtCommand<NxtBankCard> {

   public TokenizeBankCardCommand(BankCard card) {
      super(card);
   }

   @Override
   protected void run(CommandCallback<NxtBankCard> callback) throws Exception {
      janet.createPipe(MultifunctionNxtHttpAction.class, Schedulers.io())
            .createObservableResult(new MultifunctionNxtHttpAction(createRequestBody()))
            .map(actionResponse -> createResponseBody(actionResponse.getResponse()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private MultiRequestBody createRequestBody() {
      return ImmutableMultiRequestBody.builder()
            .sessionToken(getSessionToken())
            .multiRequestElements(NxtBankCardHelper.getDataForTokenization(card))
            .build();
   }

   private NxtBankCard createResponseBody(MultiResponseBody response) {
      return TokenizedBankCard.from(card, response);
   }

}