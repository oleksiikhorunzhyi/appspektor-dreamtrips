package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.worldventures.core.di.qualifier.ForApplication;
import com.worldventures.core.janet.SessionActionPipeCreator;
import com.worldventures.core.modules.auth.api.command.LogoutAction;
import com.worldventures.core.modules.auth.service.AuthInteractor;
import com.worldventures.core.modules.settings.storage.SettingsStorage;
import com.worldventures.core.service.analytics.AnalyticsInteractor;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;
import com.worldventures.dreamtrips.core.navigation.service.DialogNavigatorInteractor;
import com.worldventures.dreamtrips.core.utils.DTCookieManager;
import com.worldventures.dreamtrips.modules.common.delegate.ReplayEventDelegatesWiper;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.OfflineWarningDelegate;
import com.worldventures.dreamtrips.modules.common.service.InitializerInteractor;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerEventDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerImagesProcessedEventDelegate;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegateImpl;
import com.worldventures.dreamtrips.modules.dtl.service.AttributesInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.ClearMemoryInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FullMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsFacadeInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsRequestSourceInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library = true, complete = false)
public class ManagerModule {

   @Provides
   public PhotoUploadingManagerS3 providePhotoUploadingManagerS3(@ForApplication Context context, TransferUtility transferUtility) {
      return new PhotoUploadingManagerS3(context, transferUtility);
   }

   @Singleton
   @Provides
   ClearMemoryInteractor provideClearMemoryInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new ClearMemoryInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   DtlLocationInteractor provideDtlLocationInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new DtlLocationInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   MerchantsInteractor provideMerchantsInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         DtlLocationInteractor locationInteractor, ClearMemoryInteractor clearMemoryInteractor) {
      return new MerchantsInteractor(sessionActionPipeCreator, locationInteractor, clearMemoryInteractor);
   }

   @Singleton
   @Provides
   FullMerchantInteractor provideFullMerchantInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         DtlLocationInteractor dtlLocationInteractor) {
      return new FullMerchantInteractor(sessionActionPipeCreator, dtlLocationInteractor);
   }

   @Singleton
   @Provides
   DtlTransactionInteractor provideDtlTransactionInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new DtlTransactionInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   FilterDataInteractor provideFilterDataInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         AnalyticsInteractor analyticsInteractor, DtlLocationInteractor dtlLocationInteractor, MerchantsRequestSourceInteractor merchantsRequestSourceInteractor,
         SettingsStorage settingsStorage) {
      return new FilterDataInteractor(sessionActionPipeCreator, analyticsInteractor, dtlLocationInteractor, merchantsRequestSourceInteractor,
            settingsStorage);
   }

   @Singleton
   @Provides
   MerchantsFacadeInteractor provideMerchantsFacadeInteractor(MerchantsRequestSourceInteractor merchantsRequestSourceInteractor,
         FilterDataInteractor filterDataInteractor, MerchantsInteractor merchantsInteractor, DtlLocationInteractor locationInteractor) {
      return new MerchantsFacadeInteractor(merchantsRequestSourceInteractor, filterDataInteractor, merchantsInteractor, locationInteractor);
   }

   @Singleton
   @Provides
   AttributesInteractor provideAttributesInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         FilterDataInteractor filterDataInteractor, DtlLocationInteractor dtlLocationInteractor) {
      return new AttributesInteractor(sessionActionPipeCreator, filterDataInteractor, dtlLocationInteractor);
   }

   @Singleton
   @Provides
   MerchantsRequestSourceInteractor provideMerchantsRequestSourceInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new MerchantsRequestSourceInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   PresentationInteractor providePresentationInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new PresentationInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   LocationDelegate provideLocationDelegate(@ForApplication Context context) {
      return new LocationDelegateImpl(context);
   }

   @Provides
   @Singleton
   DTCookieManager provideCookieManager(@ForApplication Context context) {
      return new DTCookieManager(context);
   }

   @Provides
   @Singleton
   MediaPickerEventDelegate provideMediaPickerManager() {
      return new MediaPickerEventDelegate();
   }

   @Provides
   @Singleton
   MediaPickerImagesProcessedEventDelegate provideMediaPickerImagesProcessedDelegate(ReplayEventDelegatesWiper wiper) {
      return new MediaPickerImagesProcessedEventDelegate(wiper);
   }

   @Provides
   @Singleton
   DialogNavigatorInteractor provideDialogNavigatorInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new DialogNavigatorInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   OfflineWarningDelegate provideOfflineWarningDelegate() {
      return new OfflineWarningDelegate();
   }

   @Provides
   @Singleton
   InitializerInteractor provideInitializerInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         AuthInteractor loginInteractor) {
      return new InitializerInteractor(sessionActionPipeCreator, loginInteractor);
   }

   // clear after logout actions
   @Provides(type = Provides.Type.SET)
   LogoutAction provideOfflineWarningDelegateLogoutAction(OfflineWarningDelegate offlineWarningDelegate) {
      return offlineWarningDelegate::resetState;
   }

   @Provides(type = Provides.Type.SET)
   LogoutAction provideCookieManagerLogoutAction(DTCookieManager cookieManager) {
      return cookieManager::clearCookies;
   }
}
