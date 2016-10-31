package com.worldventures.dreamtrips.wallet.service.command;


import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.domain.entity.card.ImmutableBankCard;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.EditRecordAction;
import io.techery.janet.smartcard.model.Record;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class UpdateBankCardCommand extends Command<BankCard> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject MapperyContext mapperyContext;

   private BankCard bankCard;

   private UpdateBankCardCommand(BankCard bankCard) {
      this.bankCard = bankCard;
   }

   public static UpdateBankCardCommand updateNickName(BankCard bankCard, String nickName) {
      return new UpdateBankCardCommand(ImmutableBankCard.builder().from(bankCard).title(nickName).build());
   }

   @Override
   protected void run(CommandCallback<BankCard> callback) throws Throwable {
      Observable.just(bankCard)
            .map(it -> mapperyContext.convert(it, Record.class))
            .flatMap(record -> janet.createPipe(EditRecordAction.class)
                  .createObservableResult(new EditRecordAction(record)))
            .map(recordAction -> mapperyContext.convert(recordAction.record, BankCard.class))
            .subscribe(callback::onSuccess, callback::onFail);
   }
}
