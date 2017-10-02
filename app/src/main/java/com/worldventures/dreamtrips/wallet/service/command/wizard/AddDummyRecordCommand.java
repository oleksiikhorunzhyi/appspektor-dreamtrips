package com.worldventures.dreamtrips.wallet.service.command.wizard;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.command.FetchFirmwareVersionCommand;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.AddRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.DefaultRecordIdCommand;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.di.WalletJanetModule.JANET_WALLET;
import static com.worldventures.dreamtrips.wallet.domain.entity.record.FinancialService.GENERIC;
import static com.worldventures.dreamtrips.wallet.util.SCFirmwareUtils.obtainRecordVersion;
import static rx.Observable.just;

@CommandAction
public class AddDummyRecordCommand extends Command<Void> implements InjectableAction {

   @Inject RecordInteractor recordInteractor;
   @Inject @Named(JANET_WALLET) Janet janet;

   private final SmartCardUser user;
   private final boolean onlyToCache;

   public AddDummyRecordCommand(SmartCardUser user, boolean onlyToCache) {
      this.user = user;
      this.onlyToCache = onlyToCache;
   }


   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      fetchFirmwareVersion()
            .flatMap(this::createDummyCards)
            .flatMap(records -> sendCard(records.get(0), records.get(1)))
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private Observable<Void> sendCard(Record dummyCard1, Record dummyCard2) {
      // !!!! first card should be default !!!
      if (onlyToCache) { // because synchronization of sample card is broken
         return recordInteractor.cardsListPipe()
               .createObservableResult(RecordListCommand.add(dummyCard1))
               .flatMap(c -> recordInteractor.cardsListPipe()
                     .createObservableResult(RecordListCommand.add(dummyCard2)))
               .flatMap(c -> recordInteractor.defaultRecordIdPipe()
                     .createObservableResult(DefaultRecordIdCommand.set(DummyRecordCreator.defaultRecordId())))
               .map(command -> (Void) null)
               .onErrorReturn(throwable -> null);
      } else {
         return addDummyCard(dummyCard1, true)
               .flatMap(it -> addDummyCard(dummyCard2, false));
      }
   }

   private Observable<SmartCardFirmware> fetchFirmwareVersion() {
      return janet.createPipe(FetchFirmwareVersionCommand.class)
            .createObservableResult(new FetchFirmwareVersionCommand())
            .map(Command::getResult);
   }

   private Observable<List<Record>> createDummyCards(SmartCardFirmware firmware) {
      final String version = obtainRecordVersion(firmware.nordicAppVersion());

      return just(DummyRecordCreator.createRecords(user, version));
   }

   private Observable<Void> addDummyCard(Record dummyCard, boolean isDefault) {
      return recordInteractor.addRecordPipe()
            .createObservableResult(new AddRecordCommand.Builder()
                  .setRecord(dummyCard)
                  .setCvv(dummyCard.cvv())
                  .setSetAsDefaultRecord(isDefault)
                  .setRecordName(dummyCard.nickName())
                  .create()
            )
            .map(command -> (Void) null)
            .onErrorReturn(throwable -> null);
   }

}
