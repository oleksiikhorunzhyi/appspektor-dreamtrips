package com.worldventures.dreamtrips.wallet.di;

import com.worldventures.dreamtrips.core.janet.cache.storage.ActionStorage;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.wallet.domain.storage.AddressWithPlacesActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.DefaultRecordIdStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.DeviceStateActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.FirmwareUpdateActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardDetailsActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardFirmwareActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.SmartCardUserActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.SyncRecordsStatusActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.TermsAndConditionsActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.WalletRecordsDiskStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.RecordsStorage;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareRepository;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

import static com.worldventures.dreamtrips.wallet.di.WalletJanetModule.JANET_WALLET;

@Module(complete = false, library = true)
public class WalletActionStorageModule {

   /* Multiple action storage IS NOT SUPPORTED */

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage walletCardListStorage(RecordsStorage bankCardsStorage) {
      return new WalletRecordsDiskStorage(bankCardsStorage);
   }

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage defaultBankCardStorage(RecordsStorage bankCardsStorage) {
      return new DefaultRecordIdStorage(bankCardsStorage);
   }

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage smartCardActionStorage(SnappyRepository snappyRepository) {
      return new SmartCardActionStorage(snappyRepository);
   }

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage smartCardDetailsActionStorage(SnappyRepository snappyRepository) {
      return new SmartCardDetailsActionStorage(snappyRepository);
   }

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage termsAndConditionsActionStorage(SnappyRepository snappyRepository) {
      return new TermsAndConditionsActionStorage(snappyRepository);
   }

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage addressWithPlacesActionStorage() {
      return new AddressWithPlacesActionStorage();
   }

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage deviceStateActionStorage() {
      return new DeviceStateActionStorage();
   }

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage smartCardFirmwareActionStorage(SnappyRepository snappyRepository) {
      return new SmartCardFirmwareActionStorage(snappyRepository);
   }

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage smartCardUserActionStorage(SnappyRepository snappyRepository) {
      return new SmartCardUserActionStorage(snappyRepository);
   }

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage firmwareUpdateActionStorage(FirmwareRepository repository) {
      return new FirmwareUpdateActionStorage(repository);
   }

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage syncRecordsStatusActionStorage(SnappyRepository snappyRepository) {
      return new SyncRecordsStatusActionStorage(snappyRepository);
   }
}
