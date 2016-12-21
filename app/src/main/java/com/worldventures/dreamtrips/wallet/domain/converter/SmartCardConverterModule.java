package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class SmartCardConverterModule {

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideBankCardToRecordConverter() {
      return new BankCardToRecordConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideBankInfoConverter() {
      return new BankInfoConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideSmartCardDetailsConverter() {
      return new SmartCardDetailsConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideSmartCardInfoToSmartCard() {
      return new SmartCardInfoToSmartCard();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideSmartCardInfoToSmartCardDetail() {
      return new SmartCardInfoToSmartCardDetail();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideRecordToBankCardConverter() {
      return new RecordToBankCardConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideProfileAddressToUserAddressConverter() {
      return new ProfileAddressToUserAddressConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideFirmwareRepsonseToFirmwareConverter() {
      return new FirmwareResponseToFirmwareDataConverter();
   }


   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideSmartCardUserToUserConverter() {
      return new SmartCardUserToUserConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideUserToSmartCardUserConverter() {
      return new UserToSmartCardUserConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideSmartCardFirmwareConverter() {
      return new SmartCardFirmwareConverter();
   }
}
