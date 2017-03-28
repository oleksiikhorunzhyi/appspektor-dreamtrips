package com.worldventures.dreamtrips.wallet.service.command.record;

import com.worldventures.dreamtrips.core.janet.dagger.InjectableAction;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.analytics.tokenization.ActionType;
import com.worldventures.dreamtrips.wallet.domain.entity.AddressInfo;
import com.worldventures.dreamtrips.wallet.domain.entity.record.ImmutableRecord;
import com.worldventures.dreamtrips.wallet.domain.entity.record.Record;
import com.worldventures.dreamtrips.wallet.service.SmartCardInteractor;
import com.worldventures.dreamtrips.wallet.service.command.GetDefaultAddressCommand;
import com.worldventures.dreamtrips.wallet.service.command.RecordListCommand;
import com.worldventures.dreamtrips.wallet.service.command.SetDefaultCardOnDeviceCommand;
import com.worldventures.dreamtrips.wallet.util.FormatException;

import javax.inject.Inject;
import javax.inject.Named;

import io.techery.janet.Command;
import io.techery.janet.Janet;
import io.techery.janet.command.annotations.CommandAction;
import io.techery.janet.smartcard.action.records.AddRecordAction;
import io.techery.mappery.MapperyContext;
import rx.Observable;

import static com.worldventures.dreamtrips.core.janet.JanetModule.JANET_WALLET;
import static com.worldventures.dreamtrips.wallet.util.WalletValidateHelper.validateAddressInfoOrThrow;
import static com.worldventures.dreamtrips.wallet.util.WalletValidateHelper.validateCardNameOrThrow;
import static com.worldventures.dreamtrips.wallet.util.WalletValidateHelper.validateCvvOrThrow;

@CommandAction
public class AddRecordCommand extends Command<Record> implements InjectableAction {

   @Inject @Named(JANET_WALLET) Janet janet;
   @Inject MapperyContext mapperyContext;
   @Inject SmartCardInteractor smartCardInteractor;
   @Inject SnappyRepository snappyRepository;

   private final Record record;
   private final AddressInfo manualAddressInfo;
   private final boolean setAsDefaultAddress;
   private final boolean useDefaultAddress;
   private final boolean setAsDefaultRecord;

   private AddRecordCommand(Record record,
         AddressInfo manualAddressInfo,
         boolean useDefaultAddress,
         boolean setAsDefaultAddress,
         boolean setAsDefaultRecord
   ) {
      this.setAsDefaultAddress = setAsDefaultAddress;
      this.useDefaultAddress = useDefaultAddress;
      this.record = record;
      this.manualAddressInfo = manualAddressInfo;
      this.setAsDefaultRecord = setAsDefaultRecord;
   }

   public boolean setAsDefaultRecord() {
      return setAsDefaultRecord;
   }

   @Override
   protected void run(CommandCallback<Record> callback) throws Throwable {
      checkCardData();

      createRecordWithAddress()
            .flatMap(recordWithAddress -> prepareRecordForLocalStorage(recordWithAddress)
                  .flatMap((recordForLocalStorage) -> pushRecordToSmartCard(recordWithAddress)
                        .map(recordId -> ImmutableRecord.copyOf(recordForLocalStorage).withId(recordId))))
            .doOnNext(this::saveRecordLocally)
            .doOnNext(recordForLocalStorageWithId -> saveDefaultAddressIfNeed())
            .subscribe(callback::onSuccess, callback::onFail);
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

   private Observable<Record> prepareRecordForLocalStorage(Record record) {
      return smartCardInteractor.secureRecordPipe()
            .createObservableResult(SecureRecordCommand.Builder.prepareRecordForLocalStorage(record)
                  .withAnalyticsActionType(ActionType.ADD)
                  .create())
            .map(Command::getResult);
   }

   /**
    * Record without id -> AttachCardCommand -> Record id
    */
   private Observable<String> pushRecordToSmartCard(Record recordForSmartCard) {
      return Observable.just(mapperyContext.convert(recordForSmartCard, io.techery.janet.smartcard.model.Record.class))
            .flatMap(convertedRecord -> janet.createPipe(AddRecordAction.class)
                  .createObservableResult(new AddRecordAction(convertedRecord)))
            .map(it -> it.record) // id should be added in AddRecordAction
            .map((recordWithId) -> String.valueOf(recordWithId.id()))
            .flatMap(this::saveDefaultCard);
   }

   private Observable<String> saveDefaultCard(String recordId) {
      return (setAsDefaultRecord) ? smartCardInteractor.setDefaultCardOnDeviceCommandPipe()
            .createObservableResult(SetDefaultCardOnDeviceCommand.setAsDefault(recordId))
            .map(command -> recordId)
            : Observable.just(recordId);
   }

   private void saveRecordLocally(Record record) {
      smartCardInteractor.cardsListPipe()
            .send(RecordListCommand.add(record));
   }

   private void saveDefaultAddressIfNeed() {
      if (setAsDefaultAddress) {
         snappyRepository.saveDefaultAddress(manualAddressInfo);
      }
   }

   private Record createRecord(AddressInfo address) {
      return ImmutableRecord.builder()
            .from(record)
            .addressInfo(address)
            .build();
   }

   private void checkCardData() throws FormatException {
      validateCardNameOrThrow(record.nickName());
      validateAddressInfoOrThrow(manualAddressInfo);
      validateCvvOrThrow(record.cvv(), record.number());
   }

   public static class Builder {

      private Record record;
      private AddressInfo manualAddressInfo;
      private String cvv;
      private String recordName;
      private boolean useDefaultAddress;
      private boolean setAsDefaultAddress;
      private boolean setAsDefaultRecord;

      public Builder setRecord(Record record) {
         this.record = record;
         return this;
      }

      public Builder setManualAddressInfo(AddressInfo manualAddressInfo) {
         this.manualAddressInfo = manualAddressInfo;
         return this;
      }

      public Builder setRecordName(String recordName) {
         this.recordName = recordName;
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

      public Builder setSetAsDefaultRecord(boolean setAsDefaultRecord) {
         this.setAsDefaultRecord = setAsDefaultRecord;
         return this;
      }

      public AddRecordCommand create() {
         return new AddRecordCommand(ImmutableRecord.builder().from(record).cvv(cvv).nickName(recordName).build(),
               manualAddressInfo, useDefaultAddress, setAsDefaultAddress, setAsDefaultRecord);
      }
   }
}
