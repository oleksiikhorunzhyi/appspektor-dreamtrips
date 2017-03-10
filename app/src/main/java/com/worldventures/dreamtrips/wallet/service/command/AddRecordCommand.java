package com.worldventures.dreamtrips.wallet.service.command;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.ActionType;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.TokenizationAnalyticsLocationCommand;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.TokenizationCardAction;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.record.ImmutableRecord;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.nxt.NxtInteractor;
import com.worldventures.dreamtrips.wallet.service.nxt.TokenizeRecordCommand;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtBankCardHelper;
import com.worldventures.dreamtrips.wallet.service.nxt.util.NxtRecord;
import com.worldventures.dreamtrips.wallet.util.FormatException;
import com.worldventures.dreamtrips.wallet.util.NxtMultifunctionException;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.command.annotations.CommandAction;
import rx.Observable;
import timber.log.Timber;

import static com.worldventures.dreamtrips.wallet.util.WalletValidateHelper.validateAddressInfoOrThrow;
import static com.worldventures.dreamtrips.wallet.util.WalletValidateHelper.validateCardNameOrThrow;
import static com.worldventures.dreamtrips.wallet.util.WalletValidateHelper.validateCvvOrThrow;

@CommandAction
public class AddRecordCommand extends Command<Record> implements InjectableAction {

   @Inject SmartCardInteractor smartCardInteractor;
   @Inject NxtInteractor nxtInteractor;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject SnappyRepository snappyRepository;

   private final Record record;
   private final AddressInfo manualAddressInfo;
   private final String cvv;
   private final String cardName;
   private final boolean setAsDefaultAddress;
   private final boolean useDefaultAddress;
   private final boolean setAsDefaultRecord;

   private AddRecordCommand(Record record,
         AddressInfo manualAddressInfo,
         String cardName,
         String cvv,
         boolean useDefaultAddress,
         boolean setAsDefaultAddress,
         boolean setAsDefaultRecord
   ) {
      this.setAsDefaultAddress = setAsDefaultAddress;
      this.useDefaultAddress = useDefaultAddress;
      this.cardName = cardName;
      this.cvv = cvv;
      this.record = record;
      this.manualAddressInfo = manualAddressInfo;
      this.setAsDefaultRecord = setAsDefaultRecord;
   }

   @Override
   protected void run(CommandCallback<Record> callback) throws Throwable {
      checkCardData();

      createRecordWithAddress()
            .flatMap(this::tokenizeRecord)
            .flatMap(this::pushRecord)
            .subscribe(record -> {
               saveDefaultAddressIfNeed();
               Timber.d("Record was added successfully");
               callback.onSuccess(record);
            }, throwable -> {
               Timber.e(throwable, "Record was not added");
               callback.onFail(throwable);
            });
   }

   public boolean setAsDefaultRecord() {
      return setAsDefaultRecord;
   }

   private void saveDefaultAddressIfNeed() {
      if (setAsDefaultAddress) {
         snappyRepository.saveDefaultAddress(manualAddressInfo);
      }
   }

   private Observable<Record> createRecordWithAddress() {
      if (useDefaultAddress) {
         return smartCardInteractor.getDefaultAddressCommandPipe()
               .createObservableResult(new GetDefaultAddressCommand())
               .map(defaultAddressAction -> createRecord(defaultAddressAction.getResult()));
      } else {
         return Observable.just(createRecord(manualAddressInfo));
      }
   }

   private Observable<NxtRecord> tokenizeRecord(Record record) {
      return nxtInteractor.tokenizeRecordPipe()
            .createObservableResult(new TokenizeRecordCommand(record))
            .map(Command::getResult)
            .doOnNext(this::sendTokenizationAnalytics)
            .flatMap(nxtRecord -> {
               if (nxtRecord.getResponseErrors().isEmpty()) {
                  return Observable.just(nxtRecord);
               } else {
                  return Observable.error(new NxtMultifunctionException(
                        NxtBankCardHelper.getResponseErrorMessage(nxtRecord.getResponseErrors())));
               }
            });
   }

   private void sendTokenizationAnalytics(NxtRecord nxtRecord) {
      analyticsInteractor.walletAnalyticsCommandPipe().send(new TokenizationAnalyticsLocationCommand(
            TokenizationCardAction.from(nxtRecord, ActionType.ADD, true)
      ));
   }

   private Observable<Record> pushRecord(NxtRecord nxtRecord) {
      // Record without id -> AttachCardCommand -> Record with id
      return smartCardInteractor.addRecordPipe()
            .createObservableResult(new AttachCardCommand(nxtRecord, setAsDefaultRecord))
            .map(Command::getResult);
   }

   private Record createRecord(AddressInfo address) {
      return ImmutableRecord.builder()
            .from(record)
            .cvv(cvv)
            .nickName(cardName)
            .addressInfo(address)
            .build();
   }

   private void checkCardData() throws FormatException {
      validateCardNameOrThrow(cardName);
      validateAddressInfoOrThrow(manualAddressInfo);
      validateCvvOrThrow(cvv, record.number());
   }

   public static class Builder {

      private Record record;
      private AddressInfo manualAddressInfo;
      private String cvv;
      private String cardName;
      private boolean useDefaultAddress;
      private boolean setAsDefaultAddress;
      private boolean setAsDefaultCard;

      public Builder setRecord(Record record) {
         this.record = record;
         return this;
      }

      public Builder setManualAddressInfo(AddressInfo manualAddressInfo) {
         this.manualAddressInfo = manualAddressInfo;
         return this;
      }

      public Builder setCardName(String cardName) {
         this.cardName = cardName;
         return this;
      }

      public Builder setCvv(String cvv) {
         this.cvv = cvv;
         return this;
      }

      public Builder setUseDefaultAddress(boolean useDefaultAddress) {
         this.useDefaultAddress = useDefaultAddress;
         return this;
      }

      public Builder setSetAsDefaultAddress(boolean setAsDefaultAddress) {
         this.setAsDefaultAddress = setAsDefaultAddress;
         return this;
      }

      public Builder setSetAsDefaultCard(boolean setAsDefaultCard) {
         this.setAsDefaultCard = setAsDefaultCard;
         return this;
      }

      public AddRecordCommand create() {
         return new AddRecordCommand(record, manualAddressInfo, cardName, cvv,
               useDefaultAddress, setAsDefaultAddress, setAsDefaultCard);
      }
   }
}
