package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.Card;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.AddRecordAction;
import io.techery.janet.smartcard.model.Record;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class AddListRecordCommand extends Command<List<Card>> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject MapperyContext mappery;
   @Inject SmartCardInteractor smartCardInteractor;

   private final List<Card> localCards;

   AddListRecordCommand(List<Card> localCards) {
      this.localCards = localCards;
   }

   @Override
   protected void run(CommandCallback<List<Card>> callback) throws Throwable {
      if (localCards.isEmpty()) {
         callback.onSuccess(Collections.emptyList());
         return;
      }

      Observable.from(localCards)
            .flatMap(card -> smartCardInteractor.addNativeRecordPipe()
                  .createObservableResult(new AddRecordAction(mappery.convert(card, Record.class)))
                  .map(action -> (Card) mappery.convert(action.record, BankCard.class)))
            .toList()
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
