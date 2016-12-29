package com.worldventures.dreamtrips.modules.dtl_flow.di;


import com.worldventures.dreamtrips.modules.dtl.domain.converter.AttributeConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.CoordinatesConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.CurrencyConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.DisclaimerConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.LocationsConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.MerchantConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.MerchantMediaConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.OfferConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.OperationDayConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.OperationHourConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.ThinAttributeConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.ThinMerchantConverter;
import com.worldventures.dreamtrips.modules.mapping.converter.Converter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class DtlMappingModule {

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideAttributeConverter() {
      return new AttributeConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideCoordinatesConverter() {
      return new CoordinatesConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideCurrencyConverter() {
      return new CurrencyConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideDisclaimerConverter() {
      return new DisclaimerConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideMerchantConverter() {
      return new MerchantConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideMerchantMediaConverter() {
      return new MerchantMediaConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideOfferConverter() {
      return new OfferConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideOperationDayConverter() {
      return new OperationDayConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideOperationHourConverter() {
      return new OperationHourConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideThinAttributeConverter() {
      return new ThinAttributeConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideThinMerchantConverter() {
      return new ThinMerchantConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideLocationsConverter() {
      return new LocationsConverter();
   }
}
