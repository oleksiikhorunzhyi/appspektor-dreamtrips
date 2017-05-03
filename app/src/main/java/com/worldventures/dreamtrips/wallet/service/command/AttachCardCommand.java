package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.card.BankCard;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;

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
   @Inject SmartCardInteractor interactor;

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
            .map(addedRecord -> mapperyContext.convert(addedRecord, BankCard.class))
            .flatMap(newCard -> interactor.cardsListPipe()
                  .createObservableResult(CardListCommand.add(newCard)))
            .subscribe(command -> callback.onSuccess((BankCard) command.getResult()), callback::onFail);
   }

   private Observable<Record> saveDefaultCard(Record record) {
      String cardId = String.valueOf(record.id());
      return setAsDefaultCard ?
            interactor.defaultCardIdPipe().createObservableResult(DefaultCardIdCommand.set(cardId))
                  .flatMap(defaultCardIdCommand -> interactor.setDefaultCardOnDeviceCommandPipe()
                        .createObservableResult(SetDefaultCardOnDeviceCommand.setAsDefault(cardId))
                        .map(setDefaultCardOnDeviceAction -> record))
            :
            Observable.just(record);
   }
}
