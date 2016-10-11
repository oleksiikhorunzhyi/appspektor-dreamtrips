package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.messenger.storage.dao.PhotoDAO;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.DreamSpiceService;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;
import com.worldventures.dreamtrips.core.api.SocialUploaderyManager;
import com.worldventures.dreamtrips.core.api.VideoDownloadSpiceService;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.janet.SessionActionPipeCreator;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.CirclesInteractor;
import com.worldventures.dreamtrips.core.utils.DTCookieManager;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityDelegate;
import com.worldventures.dreamtrips.modules.common.delegate.CachedEntityInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.DownloadFileInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.QueryTripsFilterDataInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.SocialCropImageManager;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.ClearDirectoryDelegate;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.OfflineWarningDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.common.view.util.PhotoPickerDelegate;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegateImpl;
import com.worldventures.dreamtrips.modules.dtl.service.AttributesInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.MerchantsInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FilterDataInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.FullMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.PresentationInteractor;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.membership.api.PhoneContactRequest;
import com.worldventures.dreamtrips.modules.tripsimages.view.util.EditPhotoTagsCallback;
import com.worldventures.dreamtrips.modules.tripsimages.view.util.PostLocationPickerCallback;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

@Module(
      injects = {
            DreamSpiceManager.class,
            DreamSpiceService.class,
            VideoDownloadSpiceService.class,
            PhotoUploadingManagerS3.class,
            SocialUploaderyManager.class,
            PhoneContactRequest.class,
            QueryTripsFilterDataInteractor.class,
      },
      library = true, complete = false)
public class ManagerModule {

   @Provides
   public DreamSpiceManager provideSpiceManager(@ForApplication Injector injector) {
      return new DreamSpiceManager(DreamSpiceService.class, injector);
   }

   @Singleton
   @Provides
   public CirclesInteractor provideQueryCirclesInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new CirclesInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   public SocialUploaderyManager provideSocialUploaderyManager(@ForApplication Injector injector) {
      return new SocialUploaderyManager(injector);
   }

   @Provides
   public PhotoUploadingManagerS3 providePhotoUploadingManagerS3(@ForApplication Injector injector) {
      return new PhotoUploadingManagerS3(injector);
   }

   @Singleton
   @Provides
   DtlLocationInteractor provideDtlLocationInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new DtlLocationInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   MerchantsInteractor provideDtlMerchantInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         DtlLocationInteractor locationInteractor, FilterDataInteractor filterDataInteractor) {
      return new MerchantsInteractor(sessionActionPipeCreator, locationInteractor, filterDataInteractor);
   }

   @Singleton
   @Provides
   FullMerchantInteractor provideFullMerchantInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new FullMerchantInteractor(sessionActionPipeCreator);
   }

   @Singleton
   @Provides
   DtlTransactionInteractor provideDtlTransactionInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         @Named(JanetModule.JANET_API_LIB) SessionActionPipeCreator sessionApiActionPipeCreator) {
      return new DtlTransactionInteractor(sessionActionPipeCreator, sessionApiActionPipeCreator);
   }

   @Singleton
   @Provides
   FilterDataInteractor provideFilterDataInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         AnalyticsInteractor analyticsInteractor, DtlLocationInteractor dtlLocationInteractor,
         SnappyRepository snappyRepository) {
      return new FilterDataInteractor(sessionActionPipeCreator, analyticsInteractor, dtlLocationInteractor,
            snappyRepository);
   }

   @Singleton
   @Provides
   AttributesInteractor provideDtlAttributesInteractor(SessionActionPipeCreator sessionActionPipeCreator,
         FilterDataInteractor filterDataInteractor, DtlLocationInteractor dtlLocationInteractor) {
      return new AttributesInteractor(sessionActionPipeCreator, filterDataInteractor, dtlLocationInteractor);
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
   FeedEntityManager provideBaseFeedEntityManager(@Global EventBus eventBus) {
      return new FeedEntityManager(eventBus);
   }

   @Provides
   @Singleton
   PhotoPickerDelegate providePhotoPickerDelegate() {
      return new PhotoPickerDelegate();
   }

   @Provides
   @Singleton
   DTCookieManager provideCookieManager(@ForApplication Context context) {
      return new DTCookieManager(context);
   }

   @Provides
   @Singleton
   ClearDirectoryDelegate provideClearDirectoryDelegate(@ForApplication Context context, PhotoDAO photoDAO) {
      return new ClearDirectoryDelegate(context, photoDAO);
   }

   @Provides
   @Singleton
   MediaPickerManager provideMediaPickerManager() {
      return new MediaPickerManager();
   }

   @Provides
   @Singleton
   SocialCropImageManager provideGlobalConfigManager(@ForApplication Context context, DreamSpiceManager dreamSpiceManager) {
      return new SocialCropImageManager(context, dreamSpiceManager);
   }

   @Provides
   @Singleton
   EditPhotoTagsCallback provideEditPhotoTagsCallback() {
      return new EditPhotoTagsCallback();
   }

   @Provides
   @Singleton
   PostLocationPickerCallback providePostLocationPickerCallback() {
      return new PostLocationPickerCallback();
   }

   @Provides
   @Singleton
   BucketInteractor provideBucketService(SessionActionPipeCreator sessionActionPipeCreator) {
      return new BucketInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   DownloadFileInteractor provideDownloadFileInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new DownloadFileInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   CachedEntityInteractor provideDownloadCachedEntityInteractor(SessionActionPipeCreator sessionActionPipeCreator) {
      return new CachedEntityInteractor(sessionActionPipeCreator);
   }

   @Provides
   @Singleton
   CachedEntityDelegate provideDownloadFileDelegate(CachedEntityInteractor cachedEntityInteractor) {
      return new CachedEntityDelegate(cachedEntityInteractor);
   }

   @Provides
   @Singleton
   OfflineWarningDelegate provideOfflineWarningDelegate() {
      return new OfflineWarningDelegate();
   }
}
