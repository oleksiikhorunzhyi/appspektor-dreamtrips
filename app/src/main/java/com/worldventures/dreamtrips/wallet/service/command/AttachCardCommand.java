package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.record.ImmutableRecord;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.record.DefaultRecordIdCommand;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtRecord;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.AddRecordAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;

@CommandAction
public class AttachCardCommand extends Command<Record> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject SmartCardInteractor interactor;

   private final NxtRecord nxtRecord;
   private final boolean setAsDefaultCard;

   public AttachCardCommand(NxtRecord card, boolean setAsDefaultCard) {
      this.nxtRecord = card;
      this.setAsDefaultCard = setAsDefaultCard;
   }

   @Override
   protected void run(CommandCallback<Record> callback) throws Throwable {
      io.techery.janet.smartcard.model.Record record = mapperyContext.convert(nxtRecord.getDetokenizedRecord(), io.techery.janet.smartcard.model.Record.class);
      janet.createPipe(AddRecordAction.class)
            .createObservableResult(new AddRecordAction(record))
            .map(it -> it.record) // id should be added in AddRecordAction
            .flatMap(this::saveDefaultCard)
            .map(addedRecord -> mapperyContext.convert(addedRecord, Record.class))
            .map(bankCardWithId -> ImmutableRecord.copyOf(nxtRecord.getTokenizedRecord())
                  .withId(bankCardWithId.id()))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<io.techery.janet.smartcard.model.Record> saveDefaultCard(io.techery.janet.smartcard.model.Record record) {
      String cardId = String.valueOf(record.id());
      return setAsDefaultCard ?
            interactor.defaultRecordIdPipe().createObservableResult(DefaultRecordIdCommand.set(cardId))
                  .flatMap(defaultCardIdCommand -> interactor.setDefaultCardOnDeviceCommandPipe()
                        .createObservableResult(SetDefaultCardOnDeviceCommand.setAsDefault(cardId))
                        .map(setDefaultCardOnDeviceAction -> record))
            :
            Observable.just(record);
   }
}
