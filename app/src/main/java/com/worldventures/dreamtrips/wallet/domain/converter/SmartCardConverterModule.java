package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class SmartCardConverterModule {

   @Provides(type = Provides.Type.SET)
   Converter provideBankCardToRecordConverter() {
      return new BankCardToRecordConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideBankInfoConverter() {
      return new BankInfoConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideSmartCardDetailsConverter() {
      return new SmartCardDetailsConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideSmartCardInfoToSmartCard() {
      return new SmartCardInfoToSmartCard();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideSmartCardInfoToSmartCardDetail() {
      return new SmartCardInfoToSmartCardDetail();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideRecordToBankCardConverter() {
      return new RecordToBankCardConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideProfileAddressToUserAddressConverter() {
      return new ProfileAddressToUserAddressConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideSmartCardUserToUserConverter() {
      return new SmartCardUserToUserConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideUserToSmartCardUserConverter() {
      return new UserToSmartCardUserConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideSmartCardFirmwareConverter() {
      return new SmartCardFirmwareConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideFinancialServiceToRecordConverter() {
      return new FinancialServiceToRecordFinancialServiceConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideRecordToFinancialServiceConverter() {
      return new RecordFinancialServiceToFinancialServiceConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideWalletLocationToSmartCardLocationConverter() {
      return new WalletLocationToSmartCardLocationConverter();
   }

}
