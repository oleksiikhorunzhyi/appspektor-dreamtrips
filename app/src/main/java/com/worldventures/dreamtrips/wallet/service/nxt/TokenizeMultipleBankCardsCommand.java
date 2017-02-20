package com.worldventures.dreamtrips.wallet.service.nxt;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.model.ImmutableMultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiRequestBody;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiRequestElement;
import com.worldventures.dreamtrips.wallet.service.nxt.model.MultiResponseBody;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCard;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCardHelper;
import com.worldventures.dreamtrips.wallet.service.nxt.util.TokenizedBankCard;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.wallet.di.JanetNxtModule.JANET_NXT;
import static com.worldventures.dreamtrips.wallet.service.nxt.MultifunctionNxtHttpAction.MULTIFUNCTION_REQUEST_ELEMENTS_LIMIT;

@CommandAction
public class TokenizeMultipleBankCardsCommand extends Command<List<NxtBankCard>> implements InjectableAction {

   @Inject @Named(JANET_NXT) Janet janet;

   private final List<? extends BankCard> cards;

   public TokenizeMultipleBankCardsCommand(List<? extends BankCard> cards) {
      this.cards = cards;
   }

   @Override
   protected void run(CommandCallback<List<NxtBankCard>> callback) throws Exception {
      List<MultiRequestElement> requestElements = Queryable.from(cards)
            .map(card -> NxtBankCardHelper.getDataForTokenization(card, card.id()))
            .fold(Collections.emptyList(), (l, r) -> Queryable.concat(l, r).toList());
      if (requestElements.isEmpty()) {
         callback.onSuccess(Collections.emptyList());
         return;
      }

      Observable.from(requestElements)
            .buffer(MULTIFUNCTION_REQUEST_ELEMENTS_LIMIT)
            .map(multiRequestElements -> janet.createPipe(MultifunctionNxtHttpAction.class, Schedulers.io())
                  .createObservableResult(new MultifunctionNxtHttpAction(createRequestBody(multiRequestElements)))
            )
            .reduce(Observable::concatWith)
            .flatMap(httpActionObservable -> httpActionObservable)
            .map(MultifunctionNxtHttpAction::getResponse)
            .toList()
            .map(this::createResponseBody)
            .subscribe(callback::onSuccess, callback::onFail);
   }


   private MultiRequestBody createRequestBody(List<MultiRequestElement> requestElements) {
      return ImmutableMultiRequestBody.builder()
            .multiRequestElements(requestElements)
            .build();
   }

   private List<NxtBankCard> createResponseBody(List<MultiResponseBody> nxtResponses) {
      return Queryable.from(cards)
            .map(bankCard -> TokenizedBankCard.from(bankCard, nxtResponses, bankCard.id()))
            .cast(NxtBankCard.class)
            .toList();
   }

}