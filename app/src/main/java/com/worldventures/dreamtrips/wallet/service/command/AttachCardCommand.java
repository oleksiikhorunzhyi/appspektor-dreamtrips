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
import rx.schedulers.Schedulers;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class AttachCardCommand extends Command<Record> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject MapperyContext mapperyContext;

   private BankCard card;
   private final boolean setAsDefaultCard;

   public AttachCardCommand(BankCard card, boolean setAsDefaultCard) {
      this.card = card;
      this.setAsDefaultCard = setAsDefaultCard;
   }

   @Override
   protected void run(CommandCallback<Record> callback) throws Throwable {
      Record record = mapperyContext.convert(card, Record.class);
      janet.createPipe(AddRecordAction.class)
            .createObservableResult(new AddRecordAction(record))
            .map(it -> it.record)
            .flatMap(this::saveDefaultCard)
            .subscribe(addedRecord -> {
               card = mapperyContext.convert(addedRecord, BankCard.class);
               callback.onSuccess(addedRecord);
            }, callback::onFail);
   }

   private Observable<Record> saveDefaultCard(Record record) {
      return setAsDefaultCard ?
            janet.createPipe(SetDefaultCardOnDeviceCommand.class, Schedulers.io())
                  .createObservableResult(SetDefaultCardOnDeviceCommand.setAsDefault(String.valueOf(record.id())))
                  .map(setDefaultCardOnDeviceAction -> record) :
            Observable.just(record);
   }

   public BankCard bankCard() {
      return card;
   }
}
