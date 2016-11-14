package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;

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
public class AttachCardCommand extends Command<BankCard> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject MapperyContext mapperyContext;

   private final BankCard card;
   private final boolean setAsDefaultCard;

   public AttachCardCommand(BankCard card, boolean setAsDefaultCard) {
      this.card = card;
      this.setAsDefaultCard = setAsDefaultCard;
   }

   @Override
   protected void run(CommandCallback<BankCard> callback) throws Throwable {
      Record record = mapperyContext.convert(card, Record.class);
      janet.createPipe(AddRecordAction.class)
            .createObservableResult(new AddRecordAction(record))
            .map(it -> it.record) // id should be added in AddRecordAction
            .flatMap(this::saveDefaultCard)
            .subscribe(addedRecord -> callback.onSuccess(mapperyContext.convert(addedRecord, BankCard.class)),
                  callback::onFail);
   }

   private Observable<Record> saveDefaultCard(Record record) {
      return setAsDefaultCard ?
            janet.createPipe(SetDefaultCardOnDeviceCommand.class)
                  .createObservableResult(SetDefaultCardOnDeviceCommand.setAsDefault(String.valueOf(record.id())))
                  .map(setDefaultCardOnDeviceAction -> record) :
            Observable.just(record);
   }
}
