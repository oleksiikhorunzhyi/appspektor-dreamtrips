package com.worldventures.dreamtrips.wallet.service.command;


import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.ActionType;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.TokenizationAnalyticsLocationCommand;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.TokenizationCardAction;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.record.ImmutableRecord;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.nxt.DetokenizeRecordCommand;
import com.worldventures.dreamtrips.wallet.service.nxt.NxtInteractor;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCardHelper;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtRecord;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.NxtMultifunctionException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.EditRecordAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;
import static com.worldventures.dreamtrips.wallet.util.WalletValidateHelper.validateAddressInfoOrThrow;
import static com.worldventures.dreamtrips.wallet.util.WalletValidateHelper.validateCardNameOrThrow;

@CommandAction
public class UpdateRecordCommand extends Command<Record> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject NxtInteractor nxtInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject MapperyContext mapperyContext;

   private Record record;

   private UpdateRecordCommand(Record record) {
      this.record = record;
   }

   public static UpdateRecordCommand updateNickName(Record record, String nickName) {
      return new UpdateRecordCommand(ImmutableRecord.builder().from(record).nickName(nickName).build());
   }

   public static UpdateRecordCommand updateAddress(Record record, AddressInfo addressInfo) {
      return new UpdateRecordCommand(
            ImmutableRecord.builder()
                  .from(record)
                  .addressInfo(addressInfo)
                  .build()
      );
   }

   @Override
   protected void run(CommandCallback<Record> callback) throws Throwable {
      checkCardData();
      detokenizeRecord(record)
            .map(detokenizedRecord -> mapperyContext.convert(detokenizedRecord, io.techery.janet.smartcard.model.Record.class))
            .flatMap(this::pushRecord)
            .subscribe(result -> callback.onSuccess(record), callback::onFail);
   }

   private void checkCardData() throws FormatException {
      validateCardNameOrThrow(record.nickName());
      validateAddressInfoOrThrow(record.addressInfo());
   }

   private Observable<Record> detokenizeRecord(Record record) {
      return nxtInteractor.detokenizeRecordPipe()
            .createObservableResult(new DetokenizeRecordCommand(record))
            .map(Command::getResult)
            .doOnNext(this::sendTokenizationAnalytics)
            .flatMap(nxtRecord -> {
               if (nxtRecord.getResponseErrors().isEmpty()) {
                  return Observable.just(nxtRecord.getDetokenizedRecord());
               } else {
                  return Observable.error(new NxtMultifunctionException(
                        NxtBankCardHelper.getResponseErrorMessage(nxtRecord.getResponseErrors())));
               }
            });
   }

   private void sendTokenizationAnalytics(NxtRecord nxtRecord) {
      analyticsInteractor.walletAnalyticsCommandPipe().send(new TokenizationAnalyticsLocationCommand(
            TokenizationCardAction.from(nxtRecord, ActionType.UPDATE, false)
      ));
   }

   private Observable<Record> pushRecord(io.techery.janet.smartcard.model.Record record) {
      return janet.createPipe(EditRecordAction.class)
            .createObservableResult(new EditRecordAction(record))
            .map(result -> mapperyContext.convert(result.record, Record.class));
   }

   public Record getRecord() {
      return record;
   }
}
