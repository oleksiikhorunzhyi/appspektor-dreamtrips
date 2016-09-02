package com.worldventures.dreamtrips.core.module;

import android.content.Context;

import com.messenger.storage.dao.PhotoDAO;
import com.techery.spares.module.Injector;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.core.api.DreamSpiceManager;
import com.worldventures.dreamtrips.core.api.DreamSpiceService;
import com.worldventures.dreamtrips.core.api.FileDownloadSpiceManager;
import com.worldventures.dreamtrips.core.api.PhotoUploadingManagerS3;
import com.worldventures.dreamtrips.core.api.SocialUploaderyManager;
import com.worldventures.dreamtrips.core.api.VideoDownloadSpiceService;
import com.worldventures.dreamtrips.core.janet.JanetModule;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.session.CirclesInteractor;
import com.worldventures.dreamtrips.core.utils.DTCookieManager;
import com.worldventures.dreamtrips.modules.bucketlist.service.BucketInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.QueryTripsFilterDataInteractor;
import com.worldventures.dreamtrips.modules.common.delegate.SocialCropImageManager;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.ClearDirectoryDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.LogoutDelegate;
import com.worldventures.dreamtrips.modules.common.view.util.MediaPickerManager;
import com.worldventures.dreamtrips.modules.common.view.util.PhotoPickerDelegate;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegate;
import com.worldventures.dreamtrips.modules.dtl.location.LocationDelegateImpl;
import com.worldventures.dreamtrips.modules.dtl.service.DtlFilterMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlMerchantInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlTransactionInteractor;
import com.worldventures.dreamtrips.modules.feed.manager.FeedEntityManager;
import com.worldventures.dreamtrips.modules.feed.service.CreatePostBodyInteractor;
import com.worldventures.dreamtrips.modules.membership.api.PhoneContactRequest;
import com.worldventures.dreamtrips.modules.trips.manager.TripFilterDataProvider;
import com.worldventures.dreamtrips.modules.trips.service.TripMapInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.view.util.EditPhotoTagsCallback;
import com.worldventures.dreamtrips.modules.tripsimages.view.util.PostLocationPickerCallback;
import com.worldventures.dreamtrips.modules.video.FileCachingDelegate;
import com.worldventures.dreamtrips.modules.video.api.DownloadFileListener;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;
import io.techery.janet.Janet;

@Module(
      injects = {DreamSpiceManager.class, DreamSpiceService.class, CirclesInteractor.class, FileCachingDelegate.class, VideoDownloadSpiceService.class, PhotoUploadingManagerS3.class, SocialUploaderyManager.class,
            //
            DownloadFileListener.class, PhoneContactRequest.class,

            LogoutDelegate.class,
            //
            DtlFilterMerchantInteractor.class, DtlMerchantInteractor.class, DtlTransactionInteractor.class,

            QueryTripsFilterDataInteractor.class,},
      library = true, complete = false)
public class ManagerModule {

   @Provides
   public DreamSpiceManager provideSpiceManager(@ForApplication Injector injector) {
      return new DreamSpiceManager(DreamSpiceService.class, injector);
   }

   @Singleton
   @Provides
   public CirclesInteractor provideQueryCirclesInteractor(Janet janet) {
      return new CirclesInteractor(janet);
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

   @Provides
   public FileDownloadSpiceManager provideVideoDownloadSpiceManager(@ForApplication Injector injector) {
      return new FileDownloadSpiceManager(VideoDownloadSpiceService.class);
   }

   @Singleton
   @Provides
   DtlMerchantInteractor dtlMerchantInteractor(Janet janet, @Named(JanetModule.JANET_API_LIB) Janet apiLibJanet, DtlLocationInteractor locationInteractor) {
      return new DtlMerchantInteractor(janet, apiLibJanet, locationInteractor);
   }

   @Singleton
   @Provides
   DtlFilterMerchantInteractor dtlFilteredMerchantInteractor(DtlMerchantInteractor dtlMerchantInteractor, DtlLocationInteractor locationInteractor, LocationDelegate locationDelegate, Janet janet) {
      return new DtlFilterMerchantInteractor(dtlMerchantInteractor, locationInteractor, locationDelegate, janet);
   }

   @Singleton
   @Provides
   DtlTransactionInteractor provideDtlTransactionInteractor(Janet janet, @Named(JanetModule.JANET_API_LIB) Janet apiLibJanet) {
      return new DtlTransactionInteractor(janet, apiLibJanet);
   }

   @Singleton
   @Provides
   DtlLocationInteractor provideDtlLocationService(Janet janet) {
      return new DtlLocationInteractor(janet);
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

   @Singleton
   @Provides
   LogoutDelegate logoutDelegate(@ForApplication Injector injector) {
      return new LogoutDelegate(injector);
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
   TripMapInteractor provideTripMapManager(Janet janet) {
      return new TripMapInteractor(janet);
   }

   @Provides
   @Singleton
   BucketInteractor provideBucketService(Janet janet) {
      return new BucketInteractor(janet);
   }

   @Provides
   @Singleton
   TripFilterDataProvider provideTripFilterDataProvider(@Global EventBus eventBus, SnappyRepository repository) {
      return new TripFilterDataProvider(eventBus, repository);
   }

   @Provides
   @Singleton
   CreatePostBodyInteractor provideCreatePostBodyInteractor(Janet janet) {
      return new CreatePostBodyInteractor(janet);
   }
}
