package com.worldventures.dreamtrips.modules.dtl_flow.di;


import com.worldventures.dreamtrips.modules.dtl.domain.converter.AttributeConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.CommentReviewConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.CoordinatesConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.CurrencyConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.DisclaimerConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.ErrorsConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.FieldErrorsConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.FlaggingConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.FormErrorsConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.InnerErrorConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.LocationsConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.MerchantConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.MerchantMediaConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.OfferConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.OperationDayConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.OperationHourConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.ReviewConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.ReviewSettingsConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.ReviewSummaryConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.ReviewTextConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.ReviewsConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.ThinAttributeConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.ThinMerchantConverter;
import com.worldventures.dreamtrips.modules.dtl.domain.converter.UserImageConverter;
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

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideReviewsConverter() {
      return new ReviewsConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideReviewConverter() {
      return new ReviewConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideErrorConverter() {
      return new ErrorsConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideInnerErrorConverter() {
      return new InnerErrorConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideFormErrorConverter() {
      return new FormErrorsConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideFieldErrorConverter() {
      return new FieldErrorsConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideReviewTextConverter() {
      return new ReviewTextConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideCommentReviewConverter() {
      return new CommentReviewConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideUserImageConverter() {
      return new UserImageConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideReviewSummaryConverter() {
      return new ReviewSummaryConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideReviewSettingsConverter() {
      return new ReviewSettingsConverter();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   Converter provideFlaggingConverterConverter() {
      return new FlaggingConverter();
   }
}
