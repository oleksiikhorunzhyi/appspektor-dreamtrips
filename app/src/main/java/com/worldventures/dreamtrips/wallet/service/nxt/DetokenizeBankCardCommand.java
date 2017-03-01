package com.worldventures.dreamtrips.wallet.service.nxt;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.model.ImmutableMultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;
import com.worldventures.dreamtrips.wallet.service.nxt.util.DetokenizedBankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCardHelper;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.wallet.di.JanetNxtModule.JANET_NXT;

@CommandAction
public class DetokenizeBankCardCommand extends Command<NxtBankCard> implements InjectableAction {

   @Inject @Named(JANET_NXT) Janet janet;

   private final BankCard card;

   public DetokenizeBankCardCommand(BankCard card) {
      this.card = card;
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
            .multiRequestElements(NxtBankCardHelper.getDataForDetokenization(card))
            .build();
   }

   private NxtBankCard createResponseBody(MultiResponseBody response) {
      return DetokenizedBankCard.from(card, response);
   }

}