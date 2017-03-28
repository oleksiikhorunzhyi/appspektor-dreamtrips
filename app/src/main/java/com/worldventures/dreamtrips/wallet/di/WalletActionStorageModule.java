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
import com.worldventures.dreamtrips.wallet.domain.storage.TermsAndConditionsActionStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.WalletRecordsDiskStorage;
import com.worldventures.dreamtrips.wallet.domain.storage.disk.RecordsStorage;
import com.worldventures.dreamtrips.wallet.service.firmware.FirmwareRepository;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class WalletActionStorageModule {

   @Provides(type = Provides.Type.SET)
   ActionStorage walletCardListStorage(RecordsStorage bankCardsStorage) {
      return new WalletRecordsDiskStorage(bankCardsStorage);
   }

   @Provides(type = Provides.Type.SET)
   ActionStorage defaultBankCardStorage(RecordsStorage bankCardsStorage) {
      return new DefaultRecordIdStorage(bankCardsStorage);
   }

   @Provides(type = Provides.Type.SET)
   ActionStorage smartCardActionStorage(SnappyRepository snappyRepository) {
      return new SmartCardActionStorage(snappyRepository);
   }

   @Provides(type = Provides.Type.SET)
   ActionStorage smartCardDetailsActionStorage(SnappyRepository snappyRepository) {
      return new SmartCardDetailsActionStorage(snappyRepository);
   }

   @Provides(type = Provides.Type.SET)
   ActionStorage termsAndConditionsActionStorage(SnappyRepository snappyRepository) {
      return new TermsAndConditionsActionStorage(snappyRepository);
   }

   @Provides(type = Provides.Type.SET)
   ActionStorage addressWithPlacesActionStorage() {
      return new AddressWithPlacesActionStorage();
   }

   @Provides(type = Provides.Type.SET)
   ActionStorage deviceStateActionStorage() {
      return new DeviceStateActionStorage();
   }

   @Provides(type = Provides.Type.SET)
   ActionStorage smartCardFirmwareActionStorage(SnappyRepository snappyRepository) {
      return new SmartCardFirmwareActionStorage(snappyRepository);
   }

   @Provides(type = Provides.Type.SET)
   ActionStorage smartCardUserActionStorage(SnappyRepository snappyRepository) {
      return new SmartCardUserActionStorage(snappyRepository);
   }

   @Provides(type = Provides.Type.SET)
   ActionStorage firmwareUpdateActionStorage(FirmwareRepository repository) {
      return new FirmwareUpdateActionStorage(repository);
   }
}
