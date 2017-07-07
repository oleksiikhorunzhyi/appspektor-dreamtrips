package com.worldventures.dreamtrips.wallet.service.command.wizard;

import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardFirmware;
import com.worldventures.dreamtrips.wallet.domain.entity.SmartCardUser;
import com.worldventures.dreamtrips.wallet.domain.entity.record.ImmutableRecord;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.command.FetchFirmwareVersionCommand;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.AddRecordCommand;
import com.worldventures.dreamtrips.wallet.service.command.record.DefaultRecordIdCommand;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

import static com.worldventures.dreamtrips.wallet.domain.entity.record.FinancialService.GENERIC;
import static com.worldventures.dreamtrips.wallet.util.SCFirmwareUtils.obtainRecordVersion;
import static rx.Observable.just;

@CommandAction
public class AddDummyRecordCommand extends Command<Void> implements InjectableAction {

   @Inject RecordInteractor recordInteractor;
   @Inject @Named(JanetModule.JANET_WALLET) Janet janet;

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
            .flatMap(records -> sendCard(records[0], records[1]))
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
                     .createObservableResult(DefaultRecordIdCommand.set("0")))
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

   private Observable<Record[]> createDummyCards(SmartCardFirmware firmware) {
      final String userLastName = user.lastName();
      final String userMiddleName = user.middleName();
      final String userFirstName = user.firstName();
      final String version = obtainRecordVersion(firmware.nordicAppVersion());

      final Record dummyCard1 = ImmutableRecord.builder()
            .id("0")
            .number("9999999999994984")
            .numberLastFourDigits("4984")
            .financialService(GENERIC)
            .expDate("02/19")
            .cvv("748")
            .version(version)
            .track1("B1234567890123445^FLYE/TEST CARD^23045211000000827000000")
            .track2("1234567890123445=230452110000827")
            .nickName("Credit Card")
            .cardHolderLastName(userLastName)
            .cardHolderMiddleName(userMiddleName)
            .cardHolderFirstName(userFirstName)
            .build();

      final Record dummyCard2 = ImmutableRecord.builder()
            .id("1")
            .number("9999999999999274")
            .numberLastFourDigits("9274")
            .expDate("06/21")
            .cvv("582")
            .version(version)
            .track1("B1234567890123445^FLYE/TEST CARD^23045211000000827000000")
            .track2("1234567890123445=230452110000827")
            .financialService(GENERIC)
            .cardHolderLastName(userLastName)
            .cardHolderMiddleName(userMiddleName)
            .cardHolderFirstName(userFirstName)
            .nickName("Credit Card")
            .build();
      return just(new Record[]{dummyCard1, dummyCard2});
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
