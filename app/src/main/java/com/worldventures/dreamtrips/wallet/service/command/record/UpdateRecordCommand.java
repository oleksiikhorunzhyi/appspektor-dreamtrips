package com.worldventures.dreamtrips.wallet.service.command.record;


import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.ActionType;
import com.worldventures.dreamtrips.wallet.domain.entity.record.ImmutableRecord;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.RecordInteractor;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.util.FormatException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.EditRecordAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;
import static com.worldventures.dreamtrips.wallet.util.WalletValidateHelper.validateCardNameOrThrow;

@CommandAction
public class UpdateRecordCommand extends Command<Void> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject MapperyContext mapperyContext;
   @Inject RecordInteractor recordInteractor;

   private Record record;

   private UpdateRecordCommand(Record record) {
      this.record = record;
   }

   public static UpdateRecordCommand updateNickName(Record record, String nickName) {
      return new UpdateRecordCommand(ImmutableRecord.builder().from(record).nickName(nickName).build());
   }

   public Record getRecord() {
      return record;
   }

   @Override
   protected void run(CommandCallback<Void> callback) throws Throwable {
      checkCardData();
      prepareRecordForSmartCard(record)
            .flatMap(this::pushRecord)
            .map(record -> (Void) null)
            .doOnNext(aVoid -> updateLocalRecord())
            .subscribe(callback::onSuccess, callback::onFail);
   }

   private void checkCardData() throws FormatException {
      validateCardNameOrThrow(record.nickName());
   }

   private Observable<io.techery.janet.smartcard.model.Record> prepareRecordForSmartCard(Record record) {
      return recordInteractor.secureRecordPipe()
            .createObservableResult(SecureRecordCommand.Builder.prepareRecordForSmartCard(record)
                  .withAnalyticsActionType(ActionType.UPDATE)
                  .create())
            .map(Command::getResult)
            .map(detokenizedRecord -> mapperyContext.convert(detokenizedRecord, io.techery.janet.smartcard.model.Record.class));
   }

   private Observable<Record> pushRecord(io.techery.janet.smartcard.model.Record record) {
      return janet.createPipe(EditRecordAction.class)
            .createObservableResult(new EditRecordAction(record))
            .map(result -> mapperyContext.convert(result.record, Record.class));
   }

   private void updateLocalRecord() {
      recordInteractor.cardsListPipe().send(RecordListCommand.edit(record));
   }

}
