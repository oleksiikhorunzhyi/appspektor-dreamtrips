package com.worldventures.dreamtrips.wallet.domain.converter;

import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class SmartCardConverterModule {

   @Provides(type = Provides.Type.SET)
   Converter provideWalletRecordToSmartCardRecordConverter() {
      return new WalletRecordToSmartCardRecordConverter();
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
   Converter provideSmartCardInfoToSmartCardDetail() {
      return new SmartCardInfoToSmartCardDetail();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideSmartCardRecordToWalletRecordConverter() {
      return new SmartCardRecordToWalletRecordConverter();
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
      return new WalletFinancialServiceToSmartCardFinancialServiceConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideRecordToFinancialServiceConverter() {
      return new SmartCardFinancialServiceToWalletFinancialServiceConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideNxtSessionConverter() {
      return new NxtSessionConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideCustomerSupportConverter() {
      return new CustomerSupportContactConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideWalletLocationToSmartCardLocationConverter() {
      return new WalletLocationToSmartCardLocationConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideAndroidAddressToWalletAddressConverter() {
      return new AndroidAddressToWalletAddressConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideWalletCoordinatesToSmartCardCoordinatesConverter() {
      return new WalletCoordinatesToSmartCardCoordinatesConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideWalletLocationTypeToSmartCardLocationTypeConverter() {
      return new WalletLocationTypeToSmartCardLocationTypeConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideSmartCardLocationToWalletLocationConverter() {
      return new SmartCardLocationToWalletLocationConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideSmartCardCoordinatesToWalletCoordinatesConverter() {
      return new SmartCardCoordinatesToWalletCoordinatesConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideSmartCardLocationTypeToWalletLocationTypeConverter() {
      return new SmartCardLocationTypeToWalletLocationTypeConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter providePlaceConverter() {
      return new ApiPlaceToWalletPlaceConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideSmartCardRecordTypeToWalletRecordTypeConverter() {
      return new SmartCardRecordTypeToWalletRecordTypeConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideWalletRecordTypeToSmartCardRecordTypeConverter() {
      return new WalletRecordTypeToSmartCardRecordTypeConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideCardUserPhoneToSmartCardUserPhoneConverter() {
      return new CardUserPhoneToSmartCardUserPhoneConverter();
   }

   @Provides(type = Provides.Type.SET)
   Converter provideSmartCardUserPhoneToCardUserPhoneConverter() {
      return new SmartCardUserPhoneToCardUserPhoneConverter();
   }
}
