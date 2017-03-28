package com.worldventures.dreamtrips.wallet.service.command.offline_mode;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.RecordsStorage;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.WalletNetworkService;
import com.worldventures.dreamtrips.wallet.service.nxt.DetokenizeMultipleRecordsCommand;
import com.worldventures.dreamtrips.wallet.service.nxt.NxtInteractor;
import com.worldventures.dreamtrips.wallet.service.nxt.TokenizeMultipleRecordsCommand;
import com.worldventures.dreamtrips.wallet.util.NetworkUnavailableException;

import java.util.List;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;

@CommandAction
public class SwitchOfflineModeCommand extends Command<Void> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject NxtInteractor nxtInteractor;
   @Inject WalletNetworkService networkService;
   @Inject RecordsStorage recordsStorage;

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      boolean offlineModeEnabled = recordsStorage.readOfflineModeState();

      List<Record> storedRecords = recordsStorage.readRecords();
      if (!storedRecords.isEmpty()) {
         if (!networkService.isAvailable()) throw new NetworkUnavailableException();

         Observable.just(storedRecords)
               .flatMap(records -> offlineModeEnabled ? tokenizeRecords(records) : detokenizeRecords(records))
               .doOnNext(processedRecords -> recordsStorage.saveRecords(processedRecords))
               .flatMap(processedRecords -> smartCardInteractor.offlineModeStatusPipe()
                     .createObservableResult(OfflineModeStatusCommand.save(!offlineModeEnabled))
                     .map(command -> (Void) null))
               .subscribe(callback::onSuccess, callback::onFail);
      } else {
         smartCardInteractor.offlineModeStatusPipe()
               .createObservableResult(OfflineModeStatusCommand.save(!offlineModeEnabled))
               .map(command -> (Void) null)
               .subscribe(callback::onSuccess, callback::onFail);
      }
   }

   private Observable<List<Record>> tokenizeRecords(List<? extends Record> records) {
      return nxtInteractor.tokenizeMultipleRecordsPipe()
            .createObservableResult(new TokenizeMultipleRecordsCommand(records, false))
            .map(Command::getResult);
   }

   private Observable<List<Record>> detokenizeRecords(List<? extends Record> records) {
      return nxtInteractor.detokenizeMultipleRecordsPipe()
            .createObservableResult(new DetokenizeMultipleRecordsCommand(records, false))
            .map(Command::getResult);
   }

}
