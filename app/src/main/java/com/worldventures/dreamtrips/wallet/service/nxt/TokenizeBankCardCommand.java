package com.worldventures.dreamtrips.wallet.service.nxt;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.model.ImmutableMultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCardHelper;
import com.worldventures.dreamtrips.wallet.service.nxt.util.TokenizedBankCard;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.wallet.di.JanetNxtModule.JANET_NXT;

@CommandAction
public class TokenizeBankCardCommand extends Command<NxtBankCard> implements InjectableAction {

   @Inject @Named(JANET_NXT) Janet janet;

   private final BankCard card;

   public TokenizeBankCardCommand(BankCard card) {
      this.card = card;
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
            .multiRequestElements(NxtBankCardHelper.getDataForTokenization(card))
            .build();
   }

   private NxtBankCard createResponseBody(MultiResponseBody response) {
      return TokenizedBankCard.from(card, response);
   }

}