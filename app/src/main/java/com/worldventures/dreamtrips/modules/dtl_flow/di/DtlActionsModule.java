package com.worldventures.dreamtrips.modules.dtl_flow.di;

import android.content.Context;

import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.UserReviewInfoProvider;
import com.worldventures.dreamtrips.modules.dtl.service.action.bundle.UserReviewInfoProviderImpl;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.AttributesActionCreator;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.CategoryHttpActionCreator;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.FullMerchantActionCreator;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.HttpActionCreator;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.LocationsActionCreator;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.MerchantsActionCreator;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.ReviewsActionCreator;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.TransactionThrstCreator;
import com.worldventures.dreamtrips.modules.dtl.service.action.creator.UrlTokenCreator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, library = true)
public class DtlActionsModule {

   @Provides(type = Provides.Type.SET)
   @Singleton
   HttpActionCreator provideMerchantsActionCreator() {
      return new MerchantsActionCreator();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   HttpActionCreator provideLocationsActionCreator() {
      return new LocationsActionCreator();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   CategoryHttpActionCreator provideAttributesActionCreator() {
      return new AttributesActionCreator();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   HttpActionCreator provideFullMerchantActionCreator() {
      return new FullMerchantActionCreator();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   HttpActionCreator provideReviewsActionCreator() {
      return new ReviewsActionCreator();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   HttpActionCreator provideUrlTokenActionCreator() {
      return new UrlTokenCreator();
   }

   @Provides(type = Provides.Type.SET)
   @Singleton
   HttpActionCreator provideTransactionActionCreator() {
      return new TransactionThrstCreator();
   }

   @Provides
   @Singleton
   UserReviewInfoProvider provideUserReviewInfoProvider(Context context) {
      return new UserReviewInfoProviderImpl(context.getApplicationContext());
   }
}
