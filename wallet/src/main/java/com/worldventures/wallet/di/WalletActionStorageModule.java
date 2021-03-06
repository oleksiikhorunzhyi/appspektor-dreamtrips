package com.worldventures.wallet.di;

import com.worldventures.janet.cache.storage.ActionStorage;
import com.worldventures.wallet.domain.storage.WalletStorage;
import com.worldventures.wallet.domain.storage.action.AboutSmartCardDataActionStorage;
import com.worldventures.wallet.domain.storage.action.AddressWithPlacesActionStorage;
import com.worldventures.wallet.domain.storage.action.DefaultRecordIdStorage;
import com.worldventures.wallet.domain.storage.action.DeviceStateActionStorage;
import com.worldventures.wallet.domain.storage.action.FirmwareUpdateActionStorage;
import com.worldventures.wallet.domain.storage.action.SmartCardActionStorage;
import com.worldventures.wallet.domain.storage.action.SmartCardFirmwareActionStorage;
import com.worldventures.wallet.domain.storage.action.SmartCardUserActionStorage;
import com.worldventures.wallet.domain.storage.action.SyncRecordsStatusActionStorage;
import com.worldventures.wallet.domain.storage.action.WalletRecordsActionStorage;
import com.worldventures.wallet.domain.storage.disk.FirmwareDataStorage;
import com.worldventures.wallet.domain.storage.disk.RecordsStorage;
import com.worldventures.wallet.service.firmware.FirmwareRepository;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

import static com.worldventures.wallet.di.WalletJanetModule.JANET_WALLET;

@Module(complete = false, library = true)
public class WalletActionStorageModule {

   /* Multiple action storage IS NOT SUPPORTED */

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage walletCardListStorage(RecordsStorage bankCardsStorage) {
      return new WalletRecordsActionStorage(bankCardsStorage);
   }

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage defaultBankCardStorage(RecordsStorage bankCardsStorage) {
      return new DefaultRecordIdStorage(bankCardsStorage);
   }

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage smartCardActionStorage(WalletStorage walletStorage) {
      return new SmartCardActionStorage(walletStorage);
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
   ActionStorage smartCardFirmwareActionStorage(WalletStorage walletStorage) {
      return new SmartCardFirmwareActionStorage(walletStorage);
   }

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage smartCardUserActionStorage(WalletStorage walletStorage) {
      return new SmartCardUserActionStorage(walletStorage);
   }

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage firmwareUpdateActionStorage(FirmwareRepository repository) {
      return new FirmwareUpdateActionStorage(repository);
   }

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage syncRecordsStatusActionStorage(WalletStorage walletStorage) {
      return new SyncRecordsStatusActionStorage(walletStorage);
   }

   @Provides(type = Provides.Type.SET)
   @Named(JANET_WALLET)
   ActionStorage aboutSmartCardDataActionStorage(FirmwareDataStorage persistentStorage) {
      return new AboutSmartCardDataActionStorage(persistentStorage);
   }
}
